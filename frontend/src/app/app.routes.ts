import { Routes } from '@angular/router';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './services/auth.service';

import { LoginComponent } from './components/login.component';
import { ClienteComponent } from './components/cliente.component';
import { PersonalComponent } from './components/personal.component';

/** Guard simple: exige sesion iniciada; si no, manda a /login. */
const sesionGuard = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.identidad() ? true : router.createUrlTree(['/login']);
};

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'cliente', component: ClienteComponent, canActivate: [sesionGuard] },
  { path: 'personal', component: PersonalComponent, canActivate: [sesionGuard] },
  { path: '**', redirectTo: 'login' }
];
