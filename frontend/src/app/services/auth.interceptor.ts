import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Interceptor funcional que agrega el encabezado Authorization (HTTP Basic)
 * a todas las peticiones hacia la API, usando las credenciales guardadas.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = sessionStorage.getItem('basicAuth');
  if (token && req.url.includes('/api/')) {
    req = req.clone({ setHeaders: { Authorization: token } });
  }
  return next(req);
};
