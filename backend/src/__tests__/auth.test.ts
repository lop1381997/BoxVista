import test from 'node:test';
import assert from 'node:assert/strict';
import { AddressInfo } from 'node:net';

process.env.NODE_ENV = 'test';
process.env.JWT_SECRET = 'test-secret';

import { app, initDatabase } from '../app';
import { sequelize } from '../db';

let baseUrl = '';
let server: ReturnType<typeof app.listen>;

test.before(async () => {
  await initDatabase(true);
  server = app.listen(0);
  const addr = server.address() as AddressInfo;
  baseUrl = `http://127.0.0.1:${addr.port}`;
});

test.after(async () => {
  await new Promise<void>((resolve, reject) => {
    server.close((err?: Error) => (err ? reject(err) : resolve()));
  });
  await sequelize.close();
});

test('register + login returns a JWT token', async () => {
  const email = 'auth.user@example.com';
  const password = 'StrongPass123!';

  const registerRes = await fetch(`${baseUrl}/api/auth/register`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });

  assert.equal(registerRes.status, 201);
  const registerBody = await registerRes.json() as { token?: string };
  assert.ok(registerBody.token);

  const loginRes = await fetch(`${baseUrl}/api/auth/login`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });

  assert.equal(loginRes.status, 200);
  const loginBody = await loginRes.json() as { token?: string };
  assert.ok(loginBody.token);
});

test('protected write endpoint rejects missing token and accepts valid token', async () => {
  const email = 'writer.user@example.com';
  const password = 'AnotherStrongPass123!';

  const registerRes = await fetch(`${baseUrl}/api/auth/register`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });
  const registerBody = await registerRes.json() as { token: string };

  const unauthorizedCreate = await fetch(`${baseUrl}/api/boxes`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify({ name: 'Caja protegida', description: 'Debe requerir token' }),
  });
  assert.equal(unauthorizedCreate.status, 401);

  const authorizedCreate = await fetch(`${baseUrl}/api/boxes`, {
    method: 'POST',
    headers: {
      'content-type': 'application/json',
      authorization: `Bearer ${registerBody.token}`,
    },
    body: JSON.stringify({ name: 'Caja protegida', description: 'Creada con token' }),
  });

  assert.equal(authorizedCreate.status, 201);
  const createdBox = await authorizedCreate.json() as { id: number; userId: number };
  assert.ok(createdBox.userId);
});

test('boxes are scoped to the authenticated user', async () => {
  const firstRegisterRes = await fetch(`${baseUrl}/api/auth/register`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify({ email: 'first.owner@example.com', password: 'StrongPass123!' }),
  });
  const firstRegisterBody = await firstRegisterRes.json() as { token: string };

  const secondRegisterRes = await fetch(`${baseUrl}/api/auth/register`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify({ email: 'second.owner@example.com', password: 'StrongPass123!' }),
  });
  const secondRegisterBody = await secondRegisterRes.json() as { token: string };

  const createRes = await fetch(`${baseUrl}/api/boxes`, {
    method: 'POST',
    headers: {
      'content-type': 'application/json',
      authorization: `Bearer ${firstRegisterBody.token}`,
    },
    body: JSON.stringify({
      name: 'Caja privada',
      description: 'Solo la ve su usuario',
      objetos: [{ nombre: 'Objeto privado', state: true }],
    }),
  });

  assert.equal(createRes.status, 201);
  const createdBox = await createRes.json() as { id: number; userId: number; objetos: Array<{ id: number }> };
  assert.ok(createdBox.id);
  assert.ok(createdBox.userId);

  const firstListRes = await fetch(`${baseUrl}/api/boxes`, {
    headers: { authorization: `Bearer ${firstRegisterBody.token}` },
  });
  assert.equal(firstListRes.status, 200);
  const firstList = await firstListRes.json() as Array<{ id: number }>;
  assert.equal(firstList.some((box) => box.id === createdBox.id), true);

  const secondListRes = await fetch(`${baseUrl}/api/boxes`, {
    headers: { authorization: `Bearer ${secondRegisterBody.token}` },
  });
  assert.equal(secondListRes.status, 200);
  const secondList = await secondListRes.json() as Array<{ id: number }>;
  assert.equal(secondList.some((box) => box.id === createdBox.id), false);

  const secondReadRes = await fetch(`${baseUrl}/api/boxes/${createdBox.id}`, {
    headers: { authorization: `Bearer ${secondRegisterBody.token}` },
  });
  assert.equal(secondReadRes.status, 404);

  const objectId = createdBox.objetos[0].id;
  const secondObjectUpdateRes = await fetch(`${baseUrl}/api/boxes/${createdBox.id}/objects/${objectId}`, {
    method: 'PUT',
    headers: {
      'content-type': 'application/json',
      authorization: `Bearer ${secondRegisterBody.token}`,
    },
    body: JSON.stringify({ nombre: 'Intento ajeno', state: false }),
  });
  assert.equal(secondObjectUpdateRes.status, 404);
});
