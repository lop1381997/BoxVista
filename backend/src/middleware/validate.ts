import { AnyZodObject } from 'zod';
import { Request, Response, NextFunction } from 'express';

export const validate =
  (schema: AnyZodObject) =>
  (req: Request, res: Response, next: NextFunction) => {
    const result = schema.safeParse(req.body);
    if (!result.success) {
      return res.status(400).json({ errors: result.error.format() });
    }
    // sobrescribimos req.body con los datos tipados
    req.body = result.data;
    next();
  };