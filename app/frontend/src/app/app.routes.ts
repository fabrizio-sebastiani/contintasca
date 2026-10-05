import { Routes } from '@angular/router';
import { authGuard } from './auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'accesso' },
  {
    path: 'accesso',
    loadComponent: () => import('./pages/login/login-page.component').then((m) => m.LoginPageComponent),
  },
  {
    path: 'conferma',
    loadComponent: () => import('./pages/confirm/confirm-page.component').then((m) => m.ConfirmPageComponent),
  },
  {
    path: '',
    loadComponent: () => import('./layout/shell.component').then((m) => m.ShellComponent),
    canActivateChild: [authGuard],
    children: [
      {
        path: 'analisi',
        loadComponent: () =>
          import('./pages/analysis/analysis-page.component').then((m) => m.AnalysisPageComponent),
      },
      {
        path: 'profilo',
        loadComponent: () => import('./pages/profile/profile-page.component').then((m) => m.ProfilePageComponent),
      },
      {
        path: 'amministrazione',
        loadComponent: () => import('./pages/admin/admin-page.component').then((m) => m.AdminPageComponent),
      },
    ],
  },
  { path: '**', redirectTo: 'accesso' },
];
