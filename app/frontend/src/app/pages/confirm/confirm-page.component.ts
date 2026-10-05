import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../api.service';
import { SessionService } from '../../session.service';
import { conferma } from '../../testi';

const SECONDI_ATTESA = 5;

@Component({
  selector: 'app-confirm-page',
  standalone: true,
  template: `
    <div class="pagina-centrata">
      <div class="card card-login" style="text-align: center;">
        <p>{{ testi.notificaInviata }}</p>
        <p class="avviso-demo">{{ testi.avvisoDemo }}</p>
        <p aria-live="polite" style="font-size: 48px; font-weight: 700; color: var(--colore-primario); margin: 12px 0;">
          {{ secondiRimasti() }}
        </p>
        <button type="button" class="btn-secondario" (click)="annulla()">{{ testi.pulsanteAnnulla }}</button>
      </div>
    </div>
  `,
})
export class ConfirmPageComponent implements OnInit, OnDestroy {
  protected readonly testi = conferma;
  protected readonly secondiRimasti = signal(SECONDI_ATTESA);
  private timerId: ReturnType<typeof setInterval> | null = null;

  constructor(
    private readonly session: SessionService,
    private readonly api: ApiService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    if (!this.session.pendingUser) {
      this.router.navigateByUrl('/accesso');
      return;
    }
    this.timerId = setInterval(() => this.tick(), 1000);
  }

  ngOnDestroy(): void {
    this.fermaTimer();
  }

  annulla(): void {
    this.fermaTimer();
    this.session.pendingUser = null;
    this.router.navigateByUrl('/accesso');
  }

  private tick(): void {
    const restanti = this.secondiRimasti() - 1;
    this.secondiRimasti.set(restanti);
    if (restanti <= 0) {
      this.fermaTimer();
      this.confermaAccesso();
    }
  }

  private fermaTimer(): void {
    if (this.timerId !== null) {
      clearInterval(this.timerId);
      this.timerId = null;
    }
  }

  private confermaAccesso(): void {
    const nome = this.session.pendingUser;
    if (!nome) {
      this.router.navigateByUrl('/accesso');
      return;
    }
    this.session.login(nome);
    this.router.navigateByUrl('/profilo');
  }
}
