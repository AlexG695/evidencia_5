import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  template: `
    <div class="topbar">
      <h1>Cafeteria Universitaria UTCH · Pedidos</h1>
      <div class="user" *ngIf="auth.identidad() as id">
        {{ id.username }} ({{ rolLegible(id.roles) }})
        <button class="secundario" style="margin-left:0.6rem" (click)="salir()">Salir</button>
      </div>
    </div>
    <div class="container">
      <router-outlet></router-outlet>
    </div>
  `
})
export class AppComponent {
  auth = inject(AuthService);
  private router = inject(Router);

  rolLegible(roles: string[]): string {
    return roles.map((r) => r.replace('ROLE_', '')).join(', ');
  }

  salir(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
