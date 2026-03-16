import test from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';

import { loadBoxes, saveBoxes } from '../storage';

test('storage roundtrip: saveBoxes + loadBoxes', () => {
  const tmpDir = fs.mkdtempSync(path.join(os.tmpdir(), 'boxvista-storage-'));
  const previousCwd = process.cwd();

  try {
    process.chdir(tmpDir);
    const payload = [
      { id: 1, name: 'Caja 1', description: 'Desc', objetos: [] },
    ];

    saveBoxes(payload);
    const loaded = loadBoxes();

    assert.deepEqual(loaded, payload);
  } finally {
    process.chdir(previousCwd);
    fs.rmSync(tmpDir, { recursive: true, force: true });
  }
});

test('loadBoxes returns empty list when file does not exist', () => {
  const tmpDir = fs.mkdtempSync(path.join(os.tmpdir(), 'boxvista-storage-empty-'));
  const previousCwd = process.cwd();

  try {
    process.chdir(tmpDir);
    assert.deepEqual(loadBoxes(), []);
  } finally {
    process.chdir(previousCwd);
    fs.rmSync(tmpDir, { recursive: true, force: true });
  }
});
