import 'dotenv/config';

import { sequelize } from '../db';
import '../models';

const force = process.argv.includes('--force');

const main = async (): Promise<void> => {
  await sequelize.authenticate();
  await sequelize.sync({ force });

  console.log(force ? 'Database tables recreated.' : 'Database tables created or already up to date.');
};

main()
  .catch((error) => {
    console.error('Failed to initialize database:', error);
    process.exitCode = 1;
  })
  .finally(async () => {
    await sequelize.close();
  });
