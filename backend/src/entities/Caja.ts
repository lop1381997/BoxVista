import { Entity, PrimaryGeneratedColumn, Column, CreateDateColumn, OneToMany } from 'typeorm';
import { CajaObjeto } from './CajaObjeto';
import { HistorialEvento } from './HistorialEvento';

import { Entity, PrimaryGeneratedColumn, Column, CreateDateColumn } from 'typeorm

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

  @OneToMany(() => CajaObjeto, (co) => co.caja)
  objetos!: CajaObjeto[];

  @OneToMany(() => HistorialEvento, (he) => he.caja)
  historial!: HistorialEvento[];

}
