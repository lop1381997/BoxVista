import { Router } from 'express';
import { z } from 'zod';
import { Box, ObjectItem } from '../models';
import { validate } from '../middleware/validate';
import { AuthenticatedRequest, requireAuth } from '../middleware/auth';

const router = Router();

// Type definitions
interface ObjInput {
  nombre: string;
  state: boolean;
}

// Zod schemas
const objetoSchema = z.object({
  nombre: z.string().min(1),
  state:  z.boolean(),
});
const boxSchema = z.object({
  name:        z.string().min(1),
  description: z.string(),
  objetos:     z.array(objetoSchema).optional(),
});

// GET all boxes owned by the authenticated user
router.get('/', requireAuth, async (req: AuthenticatedRequest, res) => {
  const userId = req.auth!.userId;
  const list = await Box.findAll({ where: { userId }, include: 'objetos' });
  res.json(list);
});

// GET one box owned by the authenticated user
router.get('/:boxId', requireAuth, async (req: AuthenticatedRequest, res) => {
  const userId = req.auth!.userId;
  const box = await Box.findOne({
    where: { id: req.params.boxId, userId },
    include: 'objetos',
  });
  if (!box) return res.status(404).json({ message: 'Box not found' });
  res.json(box);
});

// POST create box + its objetos
router.post('/', requireAuth, validate(boxSchema), async (req: AuthenticatedRequest, res) => {
  const userId = req.auth!.userId;
  const { name, description, objetos = [] } = req.body;
  const newBox = await Box.create({ name, description, userId });

  // Use bulkCreate for better performance when creating multiple objects
  const createdObjs = objetos.length > 0
    ? await ObjectItem.bulkCreate(
        (objetos as ObjInput[]).map((o: ObjInput) => ({
          nombre: o.nombre,
          state: o.state,
          boxId: newBox.id
        }))
      )
    : [];
  
  newBox.setDataValue('objetos', createdObjs);
  res.status(201).json(newBox);
});

// PUT update box (no update de objetos aquí)
router.put('/:boxId', requireAuth, validate(boxSchema), async (req: AuthenticatedRequest, res) => {
  const userId = req.auth!.userId;
  const box = await Box.findOne({ where: { id: req.params.boxId, userId } });
  if (!box) return res.status(404).json({ message: 'Box not found' });
  const { name, description } = req.body;
  await box.update({ name, description });
  res.json(box);
});

// DELETE box (cascade elimina objetos)
router.delete('/:boxId', requireAuth, async (req: AuthenticatedRequest, res) => {
  const userId = req.auth!.userId;
  const box = await Box.findOne({ where: { id: req.params.boxId, userId } });
  if (!box) return res.status(404).json({ message: 'Box not found' });
  await box.destroy();
  res.status(204).send();
});

export default router;
