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
});
