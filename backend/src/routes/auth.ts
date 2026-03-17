import { Router } from 'express';
import { z } from 'zod';
import bcrypt from 'bcryptjs';

import { User } from '../models';
import { signAuthToken } from '../middleware/auth';

const router = Router();

const authSchema = z.object({
  email: z.string().email(),
  password: z.string().min(8),
});

router.post('/register', async (req, res) => {
  const parsed = authSchema.safeParse(req.body);

  if (!parsed.success) {
    return res.status(400).json({ message: 'Invalid payload', errors: parsed.error.issues });
  }

  const { email, password } = parsed.data;
  const existing = await User.findOne({ where: { email: email.toLowerCase() } });
  if (existing) {
    return res.status(409).json({ message: 'Email already registered' });
  }

  const passwordHash = await bcrypt.hash(password, 10);
  const user = await User.create({ email: email.toLowerCase(), passwordHash });

  const token = signAuthToken({ userId: user.id, email: user.email });
  return res.status(201).json({ token });
});

router.post('/login', async (req, res) => {
  const parsed = authSchema.safeParse(req.body);

  if (!parsed.success) {
    return res.status(400).json({ message: 'Invalid payload', errors: parsed.error.issues });
  }

  const { email, password } = parsed.data;
  const user = await User.findOne({ where: { email: email.toLowerCase() } });

  if (!user) {
    return res.status(401).json({ message: 'Invalid credentials' });
  }

  const valid = await bcrypt.compare(password, user.passwordHash);
  if (!valid) {
    return res.status(401).json({ message: 'Invalid credentials' });
  }

  const token = signAuthToken({ userId: user.id, email: user.email });
  return res.status(200).json({ token });
});

export default router;
