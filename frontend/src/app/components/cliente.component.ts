import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../services/api.service';
import { AuthService } from '../services/auth.service';
import { ItemPedido, Pedido, Producto } from '../models/modelos';

/**
 * Vista del CLIENTE: consultar menu -> seleccionar -> registrar pedido
 * -> ver total -> consultar estado del pedido.
 */
@Component({
  selector: 'app-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="card">
      <h2>Menu disponible</h2>
      <table *ngIf="menu.length">
        <thead>
          <tr><th>Producto</th><th>Precio</th><th>Cantidad</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let p of menu">
            <td>{{ p.nombre }}</td>
            <td>\${{ p.precio.toFixed(2) }}</td>
            <td><input type="number" min="0" [(ngModel)]="cantidades[p.id]" style="width:70px" /></td>
          </tr>
        </tbody>
      </table>
      <p class="hint" *ngIf="!menu.length">Cargando menu...</p>

      <div class="fila-form" style="margin-top:1rem">
        <label>Cliente:</label>
        <input [(ngModel)]="cliente" placeholder="Tu nombre" />
        <button (click)="registrarPedido()" [disabled]="enviando">Registrar pedido</button>
      </div>
      <p class="msg-error" *ngIf="error">{{ error }}</p>
      <p class="msg-ok" *ngIf="okMsg">{{ okMsg }}</p>
    </div>

    <div class="card" *ngIf="ultimoPedido">
      <h2>Pedido registrado #{{ ultimoPedido.id }}</h2>
      <p>Estado: <span class="badge {{ ultimoPedido.estado }}">{{ ultimoPedido.estado }}</span></p>
      <table>
        <thead><tr><th>Producto</th><th>Cant.</th><th>Subtotal</th></tr></thead>
        <tbody>
          <tr *ngFor="let d of ultimoPedido.detalles">
            <td>{{ d.nombreProducto }}</td>
            <td>{{ d.cantidad }}</td>
            <td>\${{ d.subtotal.toFixed(2) }}</td>
          </tr>
        </tbody>
      </table>
      <p class="total">Total: \${{ ultimoPedido.total.toFixed(2) }}</p>
    </div>

    <div class="card">
      <h2>Consultar estado de un pedido</h2>
      <div class="fila-form">
        <input type="number" [(ngModel)]="idConsulta" placeholder="ID de pedido" style="width:120px" />
        <button class="secundario" (click)="consultar()">Consultar</button>
      </div>
      <div *ngIf="consultado" style="margin-top:0.8rem">
        Pedido #{{ consultado.id }} de {{ consultado.cliente }} —
        <span class="badge {{ consultado.estado }}">{{ consultado.estado }}</span>
        — Total \${{ consultado.total.toFixed(2) }}
      </div>
      <p class="msg-error" *ngIf="errorConsulta">{{ errorConsulta }}</p>
    </div>
  `
})
export class ClienteComponent implements OnInit {
  private api = inject(ApiService);
  private auth = inject(AuthService);

  menu: Producto[] = [];
  cantidades: Record<number, number> = {};
  cliente = '';
  enviando = false;
  error = '';
  okMsg = '';
  ultimoPedido: Pedido | null = null;

  idConsulta?: number;
  consultado: Pedido | null = null;
  errorConsulta = '';

  ngOnInit(): void {
    this.cliente = this.auth.identidad()?.username ?? '';
    this.api.getMenu().subscribe({
      next: (m) => (this.menu = m),
      error: () => (this.error = 'No se pudo cargar el menu.')
    });
  }

  registrarPedido(): void {
    this.error = '';
    this.okMsg = '';
    const items: ItemPedido[] = this.menu
      .filter((p) => (this.cantidades[p.id] ?? 0) > 0)
      .map((p) => ({ productoId: p.id, cantidad: Number(this.cantidades[p.id]) }));

    if (!this.cliente.trim()) { this.error = 'Indica el nombre del cliente.'; return; }
    if (!items.length) { this.error = 'Selecciona al menos un producto con cantidad valida.'; return; }

    this.enviando = true;
    this.api.crearPedido(this.cliente.trim(), items).subscribe({
      next: (p) => {
        this.enviando = false;
        this.ultimoPedido = p;
        this.okMsg = `Pedido #${p.id} registrado correctamente.`;
        this.cantidades = {};
      },
      error: (e) => {
        this.enviando = false;
        this.error = e?.error?.mensaje ?? 'No se pudo registrar el pedido.';
      }
    });
  }

  consultar(): void {
    this.errorConsulta = '';
    this.consultado = null;
    if (!this.idConsulta) { this.errorConsulta = 'Escribe un ID.'; return; }
    this.api.consultarPedido(this.idConsulta).subscribe({
      next: (p) => (this.consultado = p),
      error: () => (this.errorConsulta = 'No se encontro el pedido.')
    });
  }
}
