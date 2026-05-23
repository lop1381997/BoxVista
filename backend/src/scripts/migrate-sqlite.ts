import 'dotenv/config';
import fs from 'node:fs';
import path from 'node:path';
import sqlite3 from 'sqlite3';

import { sequelize } from '../db';
import { Box, ObjectItem, User } from '../models';

interface SqliteBox {
  id: number;
  name: string;
  description: string;
}

interface SqliteObject {
  id: number;
  nombre: string;
  state: number | boolean;
  boxId: number;
}

const sourceArg = process.argv.find((arg) => arg.startsWith('--source='));
const sourcePath = path.resolve(
  process.cwd(),
  sourceArg?.slice('--source='.length) || process.env.SQLITE_SOURCE || 'database.sqlite'
);
const replaceExisting = process.argv.includes('--replace');
const ownerEmail = process.env.MIGRATION_OWNER_EMAIL?.trim().toLowerCase();

const openSqlite = (filename: string): sqlite3.Database =>
  new sqlite3.Database(filename, sqlite3.OPEN_READONLY);

const all = <T>(db: sqlite3.Database, sql: string): Promise<T[]> =>
  new Promise((resolve, reject) => {
    db.all(sql, (error, rows: T[]) => {
      if (error) reject(error);
      else resolve(rows);
    });
  });

const closeSqlite = (db: sqlite3.Database): Promise<void> =>
  new Promise((resolve, reject) => {
    db.close((error) => {
      if (error) reject(error);
      else resolve();
    });
  });

const resetPostgresSequence = async (tableName: string, columnName: string): Promise<void> => {
  if (sequelize.getDialect() !== 'postgres') return;

  await sequelize.query(
    `
      SELECT setval(
        pg_get_serial_sequence(:tableName, :columnName),
        COALESCE((SELECT MAX("${columnName}") FROM "${tableName}"), 1),
        (SELECT COUNT(*) FROM "${tableName}") > 0
      )
    `,
    { replacements: { tableName, columnName } }
  );
};

const main = async (): Promise<void> => {
  if (!fs.existsSync(sourcePath)) {
    throw new Error(`SQLite source file not found: ${sourcePath}`);
  }

  const sqlite = openSqlite(sourcePath);

  try {
    const boxes = await all<SqliteBox>(sqlite, 'SELECT id, name, description FROM boxes ORDER BY id');
    const objects = await all<SqliteObject>(
      sqlite,
      'SELECT id, nombre, state, "boxId" AS "boxId" FROM objects ORDER BY id'
    );

    const boxIds = new Set(boxes.map((box) => box.id));
    const orphanObjects = objects.filter((object) => !boxIds.has(object.boxId));
    if (orphanObjects.length > 0) {
      throw new Error(`Found ${orphanObjects.length} objects with no matching box in ${sourcePath}.`);
    }

    await sequelize.authenticate();
    await sequelize.sync();

    if (!ownerEmail) {
      throw new Error('Set MIGRATION_OWNER_EMAIL to assign imported boxes to an existing user.');
    }

    await sequelize.transaction(async (transaction) => {
      const owner = await User.findOne({ where: { email: ownerEmail }, transaction });
      if (!owner) {
        throw new Error(`No user found for MIGRATION_OWNER_EMAIL=${ownerEmail}. Register that account first.`);
      }

      if (replaceExisting) {
        await ObjectItem.destroy({ where: {}, transaction });
        await Box.destroy({ where: {}, transaction });
      }

      if (boxes.length > 0) {
        await Box.bulkCreate(
          boxes.map((box) => ({
            id: box.id,
            name: box.name,
            description: box.description,
            userId: owner.id,
          })) as any[],
          {
            updateOnDuplicate: ['name', 'description', 'userId'],
            transaction,
          }
        );
      }

      if (objects.length > 0) {
        await ObjectItem.bulkCreate(
          objects.map((object) => ({
            id: object.id,
            nombre: object.nombre,
            state: Boolean(object.state),
            boxId: object.boxId,
          })),
          {
            updateOnDuplicate: ['nombre', 'state', 'boxId'],
            transaction,
          }
        );
      }
    });

    await resetPostgresSequence('boxes', 'id');
    await resetPostgresSequence('objects', 'id');

    console.log(
      `Migrated ${boxes.length} boxes and ${objects.length} objects from ${path.relative(process.cwd(), sourcePath)}.`
    );
  } finally {
    await closeSqlite(sqlite);
    await sequelize.close();
  }
};

main().catch((error) => {
  console.error('Failed to migrate SQLite data:', error);
  process.exitCode = 1;
});
