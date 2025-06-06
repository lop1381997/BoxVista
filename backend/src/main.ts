import 'reflect-metadata';
import fastify from 'fastify';
import cors from '@fastify/cors';
import jwt from '@fastify/jwt';

const app = fastify();

app.register(cors);
app.register(jwt, { secret: process.env.JWT_SECRET || 'secret' });

app.get('/ping', async () => ({ pong: true }));

const start = async () => {
  try {
    await app.listen({ port: 3000, host: '0.0.0.0' });
    console.log('Server running');
  } catch (err) {
    app.log.error(err);
    process.exit(1);
  }
};

start();
