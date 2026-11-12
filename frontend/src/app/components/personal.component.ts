import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../services/api.service';
import { Pedido } from '../models/modelos';

/**
 * Vista del PERSONAL: consultar pedidos y avanzar su estado
 * (RECIBIDO -> EN_PREPARACION -> LISTO -> ENTREGADO).
 */
@Component({
  selector: 'app-personal',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card">
      <h2>Pedidos</h2>
      <button class="secundario" (click)="cargar()">Actualizar</button>
      <table *ngIf="pedidos.length" style="margin-top:0.8rem">
        <thead>
          <tr><th>#</th><th>Cliente</th><th>Total</th><th>Estado</th><th>Accion</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let p of pedidos">
            <td>{{ p.id }}</td>
            <td>{{ p.cliente }}</td>
            <td>\${{ p.total.toFixed(2) }}</td>
            <td><span class="badge {{ p.estado }}">{{ p.estado }}</span></td>
            <td>
              <button *ngIf="siguiente(p.estado) as sig" (click)="avanzar(p, sig)">
                Pasar a {{ sig }}
              </button>
              <span *ngIf="!siguiente(p.estado)" class="hint">Finalizado</span>
            </td>
          </tr>
        </tbody>
      </table>
      <p class="hint" *ngIf="!pedidos.length">No hay pedidos todavia.</p>
      <p class="msg-error" *ngIf="error">{{ error }}</p>
    </div>
  `
})
export class PersonalComponent implements OnInit {
  private api = inject(ApiService);

  pedidos: Pedido[] = [];
  error = '';

  private readonly orden: Record<string, string | null> = {
    RECIBIDO: 'EN_PREPARACION',
    EN_PREPARACION: 'LISTO',
    LISTO: 'ENTREGADO',
    ENTREGADO: null
  };

  ngOnInit(): void { this.cargar(); }

  cargar(): void {
    this.error = '';
    this.api.listarPedidos().subscribe({
      next: (p) => (this.pedidos = p),
      error: () => (this.error = 'No se pudieron cargar los pedidos.')
    });
  }

  siguiente(estado: string): string | null {
    return this.orden[estado] ?? null;
  }

  avanzar(pedido: Pedido, destino: string): void {
    this.error = '';
    this.api.cambiarEstado(pedido.id, destino).subscribe({
      next: (actualizado) => {
        const i = this.pedidos.findIndex((x) => x.id === actualizado.id);
        if (i >= 0) this.pedidos[i] = actualizado;
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo cambiar el estado.')
    });
  }
}
