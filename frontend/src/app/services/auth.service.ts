import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Identidad } from '../models/modelos';

/**
 * Maneja la autenticacion con HTTP Basic.
 * Guarda las credenciales (codificadas en Base64) para que el interceptor
 * las agregue en cada peticion, y expone la identidad/rol del usuario.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API = 'http://localhost:8080/api';

  // Senal reactiva con la identidad actual (null si no hay sesion).
  readonly identidad = signal<Identidad | null>(null);

  constructor(private http: HttpClient) {
    const guardado = sessionStorage.getItem('basicAuth');
    const ident = sessionStorage.getItem('identidad');
    if (guardado && ident) {
      this.identidad.set(JSON.parse(ident));
    }
  }

  get basicAuthHeader(): string | null {
    return sessionStorage.getItem('basicAuth');
  }

  /**
   * Intenta iniciar sesion: guarda las credenciales y consulta /auth/me.
   * Si las credenciales son invalidas, el backend responde 401 y se limpia la sesion.
   */
  login(username: string, password: string): Observable<Identidad> {
    const token = 'Basic ' + btoa(`${username}:${password}`);
    sessionStorage.setItem('basicAuth', token);
    return this.http.get<Identidad>(`${this.API}/auth/me`).pipe(
      tap((ident) => {
        this.identidad.set(ident);
        sessionStorage.setItem('identidad', JSON.stringify(ident));
      })
    );
  }

  logout(): void {
    sessionStorage.removeItem('basicAuth');
    sessionStorage.removeItem('identidad');
    this.identidad.set(null);
  }

  tieneRol(rol: string): boolean {
    return this.identidad()?.roles.includes('ROLE_' + rol) ?? false;
  }
}
