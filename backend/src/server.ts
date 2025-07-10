import express from 'express';
import cors from 'cors';
import bodyParser from 'body-parser';

import { sequelize }   from './db';
import './models';      // registra modelos con Sequelize

import boxesRouter  from './routes/boxes';
import objectsRouter from './routes/objects';
import { loadBoxes } from './storage';

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(bodyParser.json());

app.use('/api/boxes',               boxesRouter);
app.use('/api/boxes/:boxId/objects', objectsRouter);

// 🔥 En desarrollo: forza borrado y recreación de tablas
sequelize.sync({ force: false }) // Cambia a `force: true` para borrar y recrear tablas
  .then(() => {
    // loadBoxes(); // Carga cajas iniciales desde storage.ts
    app.listen(PORT, () => {
      console.log(`Servidor http://localhost:${PORT}`);
    });
  })
  .catch(err => {
    console.error('Error al sincronizar la base de datos:', err);
  });