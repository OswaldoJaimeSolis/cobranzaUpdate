import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

/** Exige sesión; si la ruta declara data.roles, además uno de esos roles. */
export const sesionGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (!auth.estaAutenticado()) {
    return router.createUrlTree(['/login']);
  }
  const roles: string[] | undefined = route.data['roles'];
  if (roles && !auth.tieneRol(roles)) {
    return router.createUrlTree(['/']);
  }
  return true;
};
