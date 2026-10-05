import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionService } from './session.service';

/** RB-04: S-03, S-04 e S-05 si aprono solo dopo accesso e MFA confermati. */
export const authGuard: CanActivateFn = () => {
  const session = inject(SessionService);
  const router = inject(Router);
  if (session.isLogged()) {
    return true;
  }
  return router.parseUrl('/accesso');
};
