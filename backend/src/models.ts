import { DataTypes, Model } from 'sequelize';
import { sequelize } from './db';

// Modelo Caja
export class Box extends Model {
  public id!: number;
  public name!: string;
  public description!: string;
  public readonly objetos?: ObjectItem[];
}
Box.init({
  id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true,
  },
  name: {
    type: DataTypes.STRING, allowNull: false
  },
  description: {
    type: DataTypes.TEXT, allowNull: false
  },
}, {
  sequelize,
  tableName: 'boxes',
  timestamps: false,
});

// Modelo Objeto
export class ObjectItem extends Model {
  public id!: number;
  public nombre!: string;
  public state!: boolean;
  public boxId!: number;
}
ObjectItem.init({
  id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true,
  },
  nombre: {
    type: DataTypes.STRING, allowNull: false
  },
  state: {
    type: DataTypes.BOOLEAN, allowNull: false
  },
  boxId: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: { model: Box, key: 'id' },
    onDelete: 'CASCADE',
  },
}, {
  sequelize,
  tableName: 'objects',
  timestamps: false,
  // Add index on boxId for faster lookups when fetching objects by box
  indexes: [{ fields: ['boxId'] }],
});

// Relaciones
Box.hasMany(ObjectItem, { foreignKey: 'boxId', as: 'objetos' });
ObjectItem.belongsTo(Box, { foreignKey: 'boxId', as: 'box' });