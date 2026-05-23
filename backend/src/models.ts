import { DataTypes, Model } from 'sequelize';
import { sequelize } from './db';

export class User extends Model {
  public id!: number;
  public email!: string;
  public passwordHash!: string;
  public readonly boxes?: Box[];
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

// Modelo Caja
export class Box extends Model {
  public id!: number;
  public name!: string;
  public description!: string;
  public userId!: number;
  public readonly user?: User;
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
  userId: {
    type: DataTypes.INTEGER,
    allowNull: false,
    references: { model: 'users', key: 'id' },
    onDelete: 'CASCADE',
  },
}, {
  sequelize,
  tableName: 'boxes',
  timestamps: false,
  indexes: [{ fields: ['userId'] }],
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

// Relaciones
User.hasMany(Box, { foreignKey: 'userId', as: 'boxes', onDelete: 'CASCADE' });
Box.belongsTo(User, { foreignKey: 'userId', as: 'user' });
Box.hasMany(ObjectItem, { foreignKey: 'boxId', as: 'objetos' });
ObjectItem.belongsTo(Box, { foreignKey: 'boxId', as: 'box' });
