import { FastifyInstance } from 'fastify';
import { AppDataSource } from '../utils/db';
import { Caja } from '../entities/Caja';

export async function cajasRoutes(app: FastifyInstance) {
  const repo = AppDataSource.getRepository(Caja);

  app.addHook('onRequest', async (request, reply) => {
    try {
      await request.jwtVerify();
    } catch (err) {
      reply.code(401).send({ message: 'Unauthorized' });
    }
  });

  app.post('/cajas', async (request, reply) => {
    const { tipo } = request.body as any;
    const caja = repo.create({ tipo });
    const result = await repo.save(caja);
    reply.send({ uuid: result.uuid, tipo: result.tipo });
  });

  app.put('/cajas/:uuid/ubicacion', async (request, reply) => {
    const uuid = (request.params as any).uuid;
    const { ubicacion } = request.body as any;
    const caja = await repo.findOneBy({ uuid });
    if (!caja) return reply.code(404).send({ message: 'Not found' });
    caja.ubicacion = ubicacion;
    await repo.save(caja);
    reply.send({ uuid: caja.uuid, ubicacion: caja.ubicacion });
  });

  app.get('/cajas', async (request, reply) => {
    const cajas = await repo.find();
    reply.send({ data: cajas, page: 1 });
  });

  app.get('/cajas/:uuid', async (request, reply) => {
    const caja = await repo.findOneBy({ uuid: (request.params as any).uuid });
    if (!caja) return reply.code(404).send({ message: 'Not found' });
    reply.send(caja);
  });
}
