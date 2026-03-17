import { app, initDatabase } from './app';

const PORT = process.env.PORT || 3000;

initDatabase(false)
  .then(() => {
    app.listen(PORT, () => {
      console.log(`Servidor http://localhost:${PORT}`);
    });
  })
  .catch((err) => {
    console.error('Error al sincronizar la base de datos:', err);
  });
