import { Router } from 'express';
import { z } from 'zod';
import { Box, ObjectItem } from '../models';
import { validate } from '../middleware/validate';
import { AuthenticatedRequest, requireAuth } from '../middleware/auth';

const router = Router({ mergeParams: true });

const objetoSchema = z.object({
  nombre: z.string().min(1),
  state:  z.boolean(),
});

const findOwnedBox = (boxId: string, userId: number, includeObjects = false) =>
  Box.findOne({
    where: { id: boxId, userId },
    include: includeObjects ? 'objetos' : undefined,
  });

// GET objetos for a box
router.get('/', requireAuth, async (req: AuthenticatedRequest, res) => {
  const box = await findOwnedBox((req.params as any).boxId, req.auth!.userId, true);
  if (!box) return res.status(404).json({ message: 'Box not found' });
  res.json(box.objetos);
});

// POST new objeto
router.post('/', requireAuth, validate(objetoSchema), async (req: AuthenticatedRequest, res) => {
  const box = await findOwnedBox((req.params as any).boxId, req.auth!.userId);
  if (!box) return res.status(404).json({ message: 'Box not found' });
  const obj = await ObjectItem.create({
    nombre: req.body.nombre,
    state:  req.body.state,
    boxId:  box.id
  });
  res.status(201).json(obj);
});

// PUT update objeto
router.put('/:objectId', requireAuth, validate(objetoSchema), async (req: AuthenticatedRequest, res) => {
  const box = await findOwnedBox((req.params as any).boxId, req.auth!.userId);
  if (!box) return res.status(404).json({ message: 'Box not found' });

  const obj = await ObjectItem.findOne({
    where: { id: req.params.objectId, boxId: (req.params as any).boxId }
  });
  if (!obj) return res.status(404).json({ message: 'Object not found' });
  await obj.update(req.body);
  res.json(obj);
});

// DELETE objeto
router.delete('/:objectId', requireAuth, async (req: AuthenticatedRequest, res) => {
  const box = await findOwnedBox((req.params as any).boxId, req.auth!.userId);
  if (!box) return res.status(404).json({ message: 'Box not found' });

  const obj = await ObjectItem.findOne({
    where: { id: req.params.objectId, boxId: (req.params as any).boxId }
  });
  if (!obj) return res.status(404).json({ message: 'Object not found' });
  await obj.destroy();
  res.status(204).send();
});

export default router;
