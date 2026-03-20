import { Request, Response, NextFunction } from 'express';
import jwt from 'jsonwebtoken';

export interface AuthPayload {
  userId: number;
  email: string;
}

export interface AuthenticatedRequest extends Request {
  auth?: AuthPayload;
}

const getJwtSecret = (): string => process.env.JWT_SECRET || 'boxvista-dev-secret';

export const signAuthToken = (payload: AuthPayload): string => jwt.sign(payload, getJwtSecret(), { expiresIn: '7d' });

export const requireAuth = (req: AuthenticatedRequest, res: Response, next: NextFunction) => {
  const authHeader = req.headers.authorization;

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({ message: 'Missing or invalid authorization header' });
  }

  const token = authHeader.slice('Bearer '.length).trim();

  try {
    const decoded = jwt.verify(token, getJwtSecret()) as AuthPayload;
    req.auth = decoded;
    return next();
  } catch {
    return res.status(401).json({ message: 'Invalid or expired token' });
  }
};
