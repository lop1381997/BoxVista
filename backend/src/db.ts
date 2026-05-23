import 'dotenv/config';
import { Sequelize } from 'sequelize';

const isTest = process.env.NODE_ENV === 'test';
const databaseUrl = process.env.DATABASE_URL;
const dbHost = process.env.DB_HOST;

const parseBoolean = (value: string | undefined, defaultValue = false): boolean => {
  if (value === undefined) return defaultValue;
  return ['1', 'true', 'yes', 'on'].includes(value.toLowerCase());
};

const logging = parseBoolean(process.env.DB_LOGGING) ? console.log : false;

const sslOptions = parseBoolean(process.env.DB_SSL)
  ? {
      ssl: {
        require: true,
        rejectUnauthorized: parseBoolean(process.env.DB_SSL_REJECT_UNAUTHORIZED, true),
      },
    }
  : undefined;

export const sequelize = isTest || (!databaseUrl && !dbHost)
  ? new Sequelize({
      dialect: 'sqlite',
      storage: isTest ? ':memory:' : './database.sqlite',
      logging,
    })
  : databaseUrl
    ? new Sequelize(databaseUrl, {
        dialect: 'postgres',
        logging,
        dialectOptions: sslOptions,
      })
    : new Sequelize(
        process.env.DB_NAME || 'postgres',
        process.env.DB_USER || '',
        process.env.DB_PASSWORD || '',
        {
          dialect: 'postgres',
          host: dbHost,
          port: Number(process.env.DB_PORT || 5432),
          logging,
          dialectOptions: sslOptions,
        }
      );
