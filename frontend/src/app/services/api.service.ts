import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ItemPedido, Pedido, Producto } from '../models/modelos';

/**
 * Cliente de la API REST del backend (productos y pedidos).
 */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly API = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // ----- Menu / productos -----
  getMenu(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.API}/productos`);
  }

  // ----- Pedidos -----
  crearPedido(cliente: string, items: ItemPedido[]): Observable<Pedido> {
    return this.http.post<Pedido>(`${this.API}/pedidos`, { cliente, items });
  }

  listarPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.API}/pedidos`);
  }

  consultarPedido(id: number): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.API}/pedidos/${id}`);
  }

  cambiarEstado(id: number, estado: string): Observable<Pedido> {
    return this.http.patch<Pedido>(`${this.API}/pedidos/${id}/estado`, { estado });
  }
}
