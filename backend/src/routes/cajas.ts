import { FastifyInstance } from 'fastify';
import { AppDataSource } from '../utils/db';
import { Caja } from '../entities/Caja';
import { Objeto } from '../entities/Objeto';
import { CajaObjeto } from '../entities/CajaObjeto';
import { HistorialEvento } from '../entities/HistorialEvento';
import { detectObjects } from '../services/vision';
import * as fs from 'fs';
import * as path from 'path';

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
    const { tipo, objetos } = request.body as any;
    const caja = repo.create({ tipo });
    const result = await repo.save(caja);

    if (Array.isArray(objetos)) {
      const cajaObjetoRepo = AppDataSource.getRepository(CajaObjeto);
      for (const o of objetos) {
        const obj = await AppDataSource.getRepository(Objeto).findOneBy({ id: o.id });
        if (obj) {
          const co = cajaObjetoRepo.create({ caja: result, objeto: obj, cantidad: o.cantidad });
          await cajaObjetoRepo.save(co);
        }
      }
    }

    reply.send({ uuid: result.uuid, tipo: result.tipo });
  });

  app.put('/cajas/:uuid/ubicacion', async (request, reply) => {
    const uuid = (request.params as any).uuid;
    const { ubicacion, usuario } = request.body as any;
    const caja = await repo.findOneBy({ uuid });
    if (!caja) return reply.code(404).send({ message: 'Not found' });
    caja.ubicacion = ubicacion;
    await repo.save(caja);

    const histRepo = AppDataSource.getRepository(HistorialEvento);
    const evento = histRepo.create({ caja, tipo_evento: 'cambio_ubicacion', usuario, detalles: { ubicacion } });
    await histRepo.save(evento);

    reply.send({ uuid: caja.uuid, ubicacion: caja.ubicacion });
  });

  app.get('/cajas', async (request, reply) => {
    const { estado, tipo, ubicacion, fecha_ultima_verificacion } = request.query as any;
    const where: any = {};
    if (estado) where.estado = estado;
    if (tipo) where.tipo = tipo;
    if (ubicacion) where.ubicacion = ubicacion;
    // fecha_ultima_verificacion would require join with historial
    const cajas = await repo.find({ where });
    reply.send({ data: cajas, page: 1 });
  });

  app.get('/cajas/:uuid', async (request, reply) => {
    const caja = await repo.findOneBy({ uuid: (request.params as any).uuid });
    if (!caja) return reply.code(404).send({ message: 'Not found' });
    reply.send(caja);
  });

  app.post('/cajas/:uuid/verificar', async (request, reply) => {
    const uuid = (request.params as any).uuid;
    const caja = await repo.findOneBy({ uuid });
    if (!caja) return reply.code(404).send({ message: 'Not found' });

    const parts: string[] = [];
    const uploadDir = path.join(__dirname, '../../uploads');
    fs.mkdirSync(uploadDir, { recursive: true });
    const files = await request.saveRequestFiles();
    for (const file of files) {
      const dest = path.join(uploadDir, file.filename!);
      await fs.promises.rename(file.filepath, dest);
      parts.push(dest);
    }

    const detectados = await detectObjects(parts);
    const histRepo = AppDataSource.getRepository(HistorialEvento);
    const evento = histRepo.create({ caja, tipo_evento: 'verificacion_contenido', usuario: 'system', detalles: { detectados } });
    await histRepo.save(evento);

    reply.send({ objetos_detectados: detectados });
  });

  app.get('/cajas/:uuid/historial', async (request, reply) => {
    const uuid = (request.params as any).uuid;
    const histRepo = AppDataSource.getRepository(HistorialEvento);
    const eventos = await histRepo.find({ where: { caja: { uuid } } });
    reply.send(eventos);
  });
}
