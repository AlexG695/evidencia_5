// Modelos de datos que reflejan los DTO del backend.

export interface Producto {
  id: number;
  nombre: string;
  precio: number;
  disponible: boolean;
}

export interface ItemPedido {
  productoId: number;
  cantidad: number;
}

export interface DetalleResponse {
  productoId: number;
  nombreProducto: string;
  precioUnitario: number;
  cantidad: number;
  subtotal: number;
}

export interface Pedido {
  id: number;
  cliente: string;
  fechaHora: string;
  estado: 'RECIBIDO' | 'EN_PREPARACION' | 'LISTO' | 'ENTREGADO';
  total: number;
  detalles: DetalleResponse[];
}

export interface Identidad {
  username: string;
  roles: string[];
}
