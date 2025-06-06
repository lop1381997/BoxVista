import { FastifyInstance } from 'fastify';
import { AppDataSource } from '../utils/db';
import { User } from '../entities/User';
import * as bcrypt from 'bcryptjs';

export async function authRoutes(app: FastifyInstance) {
  const repo = AppDataSource.getRepository(User);

  app.post('/auth/register', async (request, reply) => {
    const { email, password } = request.body as any;
    const user = new User();
    user.email = email;
    user.password = await bcrypt.hash(password, 10);
    await repo.save(user);
    reply.send({ id: user.id, email: user.email });
  });

  app.post('/auth/login', async (request, reply) => {
    const { email, password } = request.body as any;
    const user = await repo.findOneBy({ email });
    if (!user || !(await bcrypt.compare(password, user.password))) {
      return reply.status(401).send({ message: 'Invalid credentials' });
    }
    const token = app.jwt.sign({ id: user.id, email: user.email });
    reply.send({ token });
  });
}
