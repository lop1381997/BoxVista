import { Entity, PrimaryGeneratedColumn, Column, CreateDateColumn } from 'typeorm';

@Entity()
export class Caja {
  @PrimaryGeneratedColumn('uuid')
  uuid!: string;

  @Column()
  tipo!: string;

  @CreateDateColumn()
  fecha_creacion!: Date;

  @Column({ nullable: true })
  ubicacion!: string;

  @Column({ default: 'pendiente' })
  estado!: string;
}
