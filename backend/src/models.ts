import { DataTypes, Model } from 'sequelize';
import { sequelize } from './db';

// Modelo Caja
export class Box extends Model {
  public id!: number;
  public name!: string;
  public description!: string;
  public ubicacion?: string;
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
  ubicacion: {
    type: DataTypes.STRING,
    allowNull: true,
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
  indexes: [{ fields: ['boxId'] }],
});

export class User extends Model {
  public id!: number;
  public email!: string;
  public passwordHash!: string;
}

User.init({
  id: {
    type: DataTypes.INTEGER,
    autoIncrement: true,
    primaryKey: true,
  },
  email: {
    type: DataTypes.STRING,
    allowNull: false,
    unique: true,
  },
  passwordHash: {
    type: DataTypes.STRING,
    allowNull: false,
  },
}, {
  sequelize,
  tableName: 'users',
  timestamps: false,
});

// Relaciones
Box.hasMany(ObjectItem, { foreignKey: 'boxId', as: 'objetos' });
ObjectItem.belongsTo(Box, { foreignKey: 'boxId', as: 'box' });
