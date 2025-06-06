import 'reflect-metadata';
import { DataSource } from 'typeorm';
import { DATABASE_URL } from './config';
import { User } from '../entities/User';
import { Caja } from '../entities/Caja';
import { Objeto } from '../entities/Objeto';
import { CajaObjeto } from '../entities/CajaObjeto';
import { HistorialEvento } from '../entities/HistorialEvento';


let dataSource: DataSource;

if (DATABASE_URL.startsWith('sqlite://')) {
  dataSource = new DataSource({
    type: 'sqlite',
    database: DATABASE_URL.replace('sqlite://', ''),
    synchronize: true,
    logging: false,
    entities: [User, Caja, Objeto, CajaObjeto, HistorialEvento],
  });
} else {
  dataSource = new DataSource({
    type: 'postgres',
    url: DATABASE_URL,
    synchronize: true,
    logging: false,
    entities: [User, Caja, Objeto, CajaObjeto, HistorialEvento],

  });
}

export const AppDataSource = dataSource;
