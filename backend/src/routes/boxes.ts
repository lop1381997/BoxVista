import { Router } from 'express';
import { z } from 'zod';
import { Box, ObjectItem } from '../models';
import { validate } from '../middleware/validate';

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
  description: z.string().min(1),
  objetos:     z.array(objetoSchema).optional(),
});

// GET all boxes
router.get('/', async (req, res) => {
  const list = await Box.findAll({ include: 'objetos' });
  res.json(list);
});

// GET one box
router.get('/:boxId', async (req, res) => {
  const box = await Box.findByPk(req.params.boxId, { include: 'objetos' });
  if (!box) return res.status(404).json({ message: 'Box not found' });
  res.json(box);
});

// POST create box + its objetos
router.post('/', validate(boxSchema), async (req, res) => {
  const { name, description, objetos = [] } = req.body;
  const newBox = await Box.create({ name, description });

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
router.put('/:boxId', validate(boxSchema), async (req, res) => {
  const box = await Box.findByPk(req.params.boxId);
  if (!box) return res.status(404).json({ message: 'Box not found' });
  await box.update(req.body);
  res.json(box);
});

// DELETE box (cascade elimina objetos)
router.delete('/:boxId', async (req, res) => {
  const box = await Box.findByPk(req.params.boxId);
  if (!box) return res.status(404).json({ message: 'Box not found' });
  await box.destroy();
  res.status(204).send();
});

export default router;