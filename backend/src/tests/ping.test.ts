import fastify from 'fastify';
import supertest from 'supertest';

describe('ping', () => {
  it('responds with pong', async () => {
    const app = fastify();
    app.get('/ping', async () => ({ pong: true }));
    await app.ready();
    const res = await supertest(app.server).get('/ping');
    expect(res.body).toEqual({ pong: true });
  });
});
