import { Injectable, signal } from '@angular/core';

const CHIAVE_SESSIONE = 'itm.nomeUtente';

/** Gestisce l'accesso simulato: il nome utente "in attesa" tra S-01 e S-02, e quello
 * confermato in sessionStorage (vale per la scheda, RB-04). */
@Injectable({ providedIn: 'root' })
export class SessionService {
  /** Nome utente restituito da /api/accesso, in attesa della conferma MFA (S-02). */
  pendingUser: string | null = null;

  readonly nomeUtente = signal<string | null>(this.leggiSessione());

  private leggiSessione(): string | null {
    return sessionStorage.getItem(CHIAVE_SESSIONE);
  }

  isLogged(): boolean {
    return this.nomeUtente() !== null;
  }

  login(nome: string): void {
    sessionStorage.setItem(CHIAVE_SESSIONE, nome);
    this.nomeUtente.set(nome);
    this.pendingUser = null;
  }

  logout(): void {
    sessionStorage.removeItem(CHIAVE_SESSIONE);
    this.nomeUtente.set(null);
    this.pendingUser = null;
  }
}
