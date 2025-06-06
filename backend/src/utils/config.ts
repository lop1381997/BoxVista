import { config } from 'dotenv';
config();

export const DATABASE_URL = process.env.DATABASE_URL || 'postgres://user:pass@localhost:5432/boxvista';
export const JWT_SECRET = process.env.JWT_SECRET || 'secret';
