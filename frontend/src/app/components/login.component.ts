import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="card login-box">
      <h2>Iniciar sesion</h2>
      <div class="campo">
        <label>Usuario</label>
        <input [(ngModel)]="username" placeholder="cliente / personal / admin" />
      </div>
      <div class="campo">
        <label>Contrasena</label>
        <input type="password" [(ngModel)]="password" (keyup.enter)="entrar()" />
      </div>
      <button (click)="entrar()" [disabled]="cargando">Entrar</button>
      <p class="msg-error" *ngIf="error">{{ error }}</p>
      <p class="hint">
        Usuarios de prueba:<br />
        cliente / cliente123 &nbsp;·&nbsp; personal / personal123 &nbsp;·&nbsp; admin / admin123
      </p>
    </div>
  `
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  username = '';
  password = '';
  error = '';
  cargando = false;

  entrar(): void {
    this.error = '';
    this.cargando = true;
    this.auth.login(this.username.trim(), this.password).subscribe({
      next: (ident) => {
        this.cargando = false;
        // Redirige segun el rol.
        if (ident.roles.includes('ROLE_PERSONAL')) {
          this.router.navigate(['/personal']);
        } else {
          this.router.navigate(['/cliente']);
        }
      },
      error: () => {
        this.cargando = false;
        this.auth.logout();
        this.error = 'Usuario o contrasena incorrectos.';
      }
    });
  }
}
