import { Entity, PrimaryGeneratedColumn, Column } from 'typeorm';

@Entity()
export class Objeto {
  @PrimaryGeneratedColumn()
  id!: number;

  @Column()
  nombre!: string;

  @Column('text')
  descripcion!: string;

  @Column()
  categoria!: string;

  @Column('float')
  peso!: number;

  @Column()
  dimensiones!: string;

  @Column('simple-array', { nullable: true })
  etiquetas?: string[];
}
