import express from 'express';
import cors from 'cors';
import bodyParser from 'body-parser';

import { sequelize } from './db';
import './models';

import boxesRouter from './routes/boxes';
import objectsRouter from './routes/objects';
import authRouter from './routes/auth';

export const app = express();

app.use(cors());
app.use(bodyParser.json());

app.use('/api/auth', authRouter);
app.use('/api/boxes', boxesRouter);
app.use('/api/boxes/:boxId/objects', objectsRouter);

export const initDatabase = async (force = false): Promise<void> => {
  await sequelize.sync({ force });
};
