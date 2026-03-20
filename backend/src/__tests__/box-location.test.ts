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

test('PUT /api/cajas/:boxId/ubicacion actualiza ubicación de caja', async () => {
  const authRes = await fetch(`${baseUrl}/auth/register`, {
    method: 'POST',
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify({ email: 'location.user@example.com', password: 'StrongPass123!' }),
  });
  const authBody = await authRes.json() as { token: string };

  const createRes = await fetch(`${baseUrl}/api/boxes`, {
    method: 'POST',
    headers: {
      'content-type': 'application/json',
      authorization: `Bearer ${authBody.token}`,
    },
    body: JSON.stringify({ name: 'Caja ubicación', description: 'inicial' }),
  });
  assert.equal(createRes.status, 201);

  const created = await createRes.json() as { id: number };

  const updateRes = await fetch(`${baseUrl}/api/cajas/${created.id}/ubicacion`, {
    method: 'PUT',
    headers: {
      'content-type': 'application/json',
      authorization: `Bearer ${authBody.token}`,
    },
    body: JSON.stringify({ ubicacion: 'Estantería A-3' }),
  });

  assert.equal(updateRes.status, 200);
  const updated = await updateRes.json() as { id: number; ubicacion: string };
  assert.equal(updated.id, created.id);
  assert.equal(updated.ubicacion, 'Estantería A-3');
});
