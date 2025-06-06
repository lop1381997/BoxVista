import 'reflect-metadata';
import fastify from 'fastify';
import cors from '@fastify/cors';
import jwt from '@fastify/jwt';
import multipart from '@fastify/multipart';
import { JWT_SECRET } from './utils/config';
import { AppDataSource } from './utils/db';
import { authRoutes } from './routes/auth';
import { cajasRoutes } from './routes/cajas';

const app = fastify();

app.register(cors);
app.register(jwt, { secret: JWT_SECRET });
app.register(multipart);

app.get('/ping', async () => ({ pong: true }));

app.register(authRoutes);
app.register(cajasRoutes);

const start = async () => {
  try {
    await AppDataSource.initialize();
    await app.listen({ port: 3000, host: '0.0.0.0' });
    console.log('Server running');
  } catch (err) {
    app.log.error(err);
    process.exit(1);
  }
};

start();
