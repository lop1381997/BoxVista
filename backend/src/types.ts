export interface Objeto {
  id: number;
  nombre: string;
  state: string;
  boxid: number;
}

export interface Caja {
  id: number;
  name: string;
  description: string;
  objetos: Objeto[];
}