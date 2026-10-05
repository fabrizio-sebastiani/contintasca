import { Component, HostListener, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { SessionService } from '../session.service';
import { NOME_APP, menu } from '../testi';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <header class="intestazione">
      <span class="nome-app">{{ nomeApp }}</span>
      <button
        type="button"
        class="pulsante-menu"
        [attr.aria-expanded]="aperto()"
        aria-controls="menu-principale"
        [attr.aria-label]="testi.etichettaPulsante"
        (click)="commuta()"
      >
        <span aria-hidden="true">&#9776;</span>
      </button>
      @if (aperto()) {
        <nav id="menu-principale">
          <ul>
            <li>
              <a routerLink="/profilo" routerLinkActive="attivo" [ariaCurrentWhenActive]="'page'" (click)="chiudi()">{{
                testi.vociProfilo
              }}</a>
            </li>
            <li>
              <a routerLink="/analisi" routerLinkActive="attivo" [ariaCurrentWhenActive]="'page'" (click)="chiudi()">{{
                testi.voceAnalisi
              }}</a>
            </li>
            <li>
              <a
                routerLink="/amministrazione"
                routerLinkActive="attivo"
                [ariaCurrentWhenActive]="'page'"
                (click)="chiudi()"
                >{{ testi.voceAmministrazione }}</a
              >
            </li>
            <li>
              <button type="button" (click)="esci()">{{ testi.voceEsci }}</button>
            </li>
          </ul>
        </nav>
      }
    </header>
    <main>
      <router-outlet />
    </main>
  `,
  styles: [
    `
      .intestazione {
        position: relative;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 12px 24px;
        background: #fff;
        border-bottom: 2px solid var(--colore-bordo);
      }
      .nome-app {
        font-weight: 700;
        font-size: 22px;
        color: var(--colore-primario-scuro);
      }
      .pulsante-menu {
        background: none;
        border: 2px solid var(--colore-primario);
        border-radius: 6px;
        font-size: 24px;
        padding: 4px 12px;
        cursor: pointer;
      }
      nav {
        position: absolute;
        top: 100%;
        right: 24px;
        background: #fff;
        border: 2px solid var(--colore-bordo);
        border-radius: 6px;
        z-index: 10;
        min-width: 200px;
      }
      nav ul {
        list-style: none;
        margin: 0;
        padding: 8px 0;
      }
      nav li a,
      nav li button {
        display: block;
        width: 100%;
        text-align: left;
        padding: 10px 18px;
        text-decoration: none;
        color: var(--colore-testo);
        background: none;
        border: none;
        font-size: 17px;
        cursor: pointer;
      }
      nav li a.attivo {
        font-weight: 700;
        color: var(--colore-primario-scuro);
      }
      nav li a:hover,
      nav li button:hover {
        background: var(--colore-sfondo);
      }
    `,
  ],
})
export class ShellComponent {
  protected readonly nomeApp = NOME_APP;
  protected readonly testi = menu;
  protected readonly aperto = signal(false);

  constructor(
    private readonly session: SessionService,
    private readonly router: Router,
  ) {}

  // Se il menu è aperto, Esc lo chiude e non arriva alla ragnatela (stopImmediatePropagation
  // impedisce agli altri gestori "window:keydown.escape" registrati dopo di ricevere l'evento).
  @HostListener('window:keydown.escape', ['$event'])
  onEscapeGlobale(evento: Event): void {
    if (this.aperto()) {
      evento.stopImmediatePropagation();
      this.chiudi();
    }
  }

  commuta(): void {
    this.aperto.set(!this.aperto());
  }

  chiudi(): void {
    this.aperto.set(false);
  }

  esci(): void {
    this.chiudi();
    this.session.logout();
    this.router.navigateByUrl('/accesso');
  }
}
