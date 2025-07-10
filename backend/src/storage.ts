import fs from 'fs';
import { Caja } from './types';

const DATA_FILE = './boxes.json';

export function loadBoxes(): Caja[] {
  return JSON.parse(fs.readFileSync(DATA_FILE, 'utf8'));
}

export function saveBoxes(boxes: Caja[]): void {
  fs.writeFileSync(DATA_FILE, JSON.stringify(boxes, null, 2));
}