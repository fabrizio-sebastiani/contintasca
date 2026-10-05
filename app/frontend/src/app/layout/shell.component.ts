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
      :host {
        display: block;
      }
      .intestazione {
        position: sticky;
        top: 0;
        z-index: 20;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 14px 32px;
        background: #fff;
        box-shadow: var(--ombra-leggera);
      }
      .nome-app {
        font-weight: 700;
        font-size: 22px;
        color: var(--colore-primario);
        letter-spacing: -0.01em;
      }
      .pulsante-menu {
        background: none;
        border: 2px solid var(--colore-primario);
        border-radius: 8px;
        font-size: 22px;
        line-height: 1;
        padding: 8px 14px;
        color: var(--colore-primario);
        cursor: pointer;
      }
      .pulsante-menu:hover {
        background: var(--colore-primario-hover-sfondo);
      }
      nav {
        position: absolute;
        top: 100%;
        right: 32px;
        margin-top: 8px;
        background: #fff;
        border: 1px solid var(--colore-bordo);
        border-radius: 12px;
        box-shadow: var(--ombra-morbida);
        z-index: 10;
        min-width: 220px;
        overflow: hidden;
      }
      nav ul {
        list-style: none;
        margin: 0;
        padding: 8px;
      }
      nav li a,
      nav li button {
        display: block;
        width: 100%;
        text-align: left;
        padding: 12px 16px;
        border-radius: 8px;
        text-decoration: none;
        color: var(--colore-testo);
        background: none;
        border: none;
        font-size: 16px;
        cursor: pointer;
      }
      nav li a.attivo {
        font-weight: 700;
        color: var(--colore-primario-scuro);
        background: var(--colore-primario-hover-sfondo);
      }
      nav li a:hover,
      nav li button:hover {
        background: var(--colore-primario-hover-sfondo);
      }
      main {
        display: block;
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
