process.env.DATABASE_URL = 'sqlite://test.db';
import fastify from 'fastify';
import jwt from '@fastify/jwt';
import multipart from '@fastify/multipart';
import { AppDataSource } from '../utils/db';
import { JWT_SECRET } from '../utils/config';
import { authRoutes } from '../routes/auth';
import { cajasRoutes } from '../routes/cajas';
import supertest from 'supertest';

const build = async () => {
  const app = fastify();
  app.register(jwt, { secret: JWT_SECRET });
  app.register(multipart);
  app.register(authRoutes);
  app.register(cajasRoutes);
  process.env.DATABASE_URL = 'sqlite://test.db';
  await AppDataSource.initialize();
  await app.ready();
  return app;
};

describe('basic routes', () => {
  it('registers and logs in a user', async () => {
    const app = await build();
    const res = await supertest(app.server)
      .post('/auth/register')
      .send({ email: 'test@example.com', password: 'test' });
    expect(res.statusCode).toBe(200);

    const login = await supertest(app.server)
      .post('/auth/login')
      .send({ email: 'test@example.com', password: 'test' });
    expect(login.statusCode).toBe(200);

    await AppDataSource.destroy();
    await app.close();
  });
});
