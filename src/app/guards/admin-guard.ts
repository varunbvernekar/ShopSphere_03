
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const authUser = authService.getCurrentUser();

  if (authUser && authUser.role === 'ADMIN') {
    return true;
  }

  // Not an admin → send to login
  return router.parseUrl('/login');
};
