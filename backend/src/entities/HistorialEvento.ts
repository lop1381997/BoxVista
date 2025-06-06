import { Entity, PrimaryGeneratedColumn, Column, ManyToOne } from 'typeorm';
import { Caja } from './Caja';

@Entity()
export class HistorialEvento {
  @PrimaryGeneratedColumn('uuid')
  uuid_evento!: string;

  @ManyToOne(() => Caja, (caja) => caja.historial)
  caja!: Caja;

  @Column()
  tipo_evento!: string;

  @Column()
  usuario!: string;

  @Column({ type: 'datetime', default: () => 'CURRENT_TIMESTAMP' })
  timestamp!: Date;

  @Column('simple-json')
  detalles!: any;
}
