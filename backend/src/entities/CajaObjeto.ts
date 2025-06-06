import { Entity, ManyToOne, Column, PrimaryGeneratedColumn } from 'typeorm';
import { Caja } from './Caja';
import { Objeto } from './Objeto';

@Entity()
export class CajaObjeto {
  @PrimaryGeneratedColumn()
  id!: number;

  @ManyToOne(() => Caja, (caja) => caja.objetos)
  caja!: Caja;

  @ManyToOne(() => Objeto)
  objeto!: Objeto;

  @Column('int')
  cantidad!: number;

  @Column({ default: 'esperado' })
  estado!: string;

  @Column({ type: 'datetime', nullable: true })
  timestamp_verificacion?: Date;
}
