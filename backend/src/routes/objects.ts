import { Router } from 'express';
import { z } from 'zod';
import { Box, ObjectItem } from '../models';
import { validate } from '../middleware/validate';

const router = Router({ mergeParams: true });

const objetoSchema = z.object({
  nombre: z.string().min(1),
  state:  z.boolean(),
});

// GET objetos for a box
router.get('/', async (req, res) => {
  const box = await Box.findByPk((req.params as any).boxId, { include: 'objetos' });
  if (!box) return res.status(404).json({ message: 'Box not found' });
  res.json(box.objetos);
});

// POST new objeto
router.post('/', validate(objetoSchema), async (req, res) => {
  const box = await Box.findByPk((req.params as any).boxId);
  if (!box) return res.status(404).json({ message: 'Box not found' });
  const obj = await ObjectItem.create({
    nombre: req.body.nombre,
    state:  req.body.state,
    boxId:  box.id
  });
  res.status(201).json(obj);
});

// PUT update objeto
router.put('/:objectId', validate(objetoSchema), async (req, res) => {
  const obj = await ObjectItem.findOne({
    where: { id: req.params.objectId, boxId: (req.params as any).boxId }
  });
  if (!obj) return res.status(404).json({ message: 'Object not found' });
  await obj.update(req.body);
  res.json(obj);
});

// DELETE objeto
router.delete('/:objectId', async (req, res) => {
  const obj = await ObjectItem.findOne({
    where: { id: req.params.objectId, boxId: (req.params as any).boxId }
  });
  if (!obj) return res.status(404).json({ message: 'Object not found' });
  await obj.destroy();
  res.status(204).send();
});

export default router;