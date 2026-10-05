import { Component, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../api.service';
import { SessionService } from '../../session.service';
import { NOME_APP, login, erroreAccesso, erroreGenerico } from '../../testi';
import { ApiError } from '../../api.models';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <div class="pagina-centrata">
      <div class="card card-login">
        <h1>{{ nomeApp }}</h1>
        <p>{{ testi.frasePresentazione }}</p>
        <p class="avviso-demo">{{ testi.avvisoDemo }}</p>
        @if (erroreGenerale()) {
          <p class="messaggio-errore" role="alert">{{ erroreGenerale() }}</p>
        }

        <form [formGroup]="form" (ngSubmit)="accedi()" novalidate>
          <div class="campo">
            <label for="nomeUtente">{{ testi.etichettaNomeUtente }}</label>
            <input
              id="nomeUtente"
              type="text"
              formControlName="nomeUtente"
              [attr.aria-invalid]="erroreNomeUtente() ? 'true' : null"
              [attr.aria-describedby]="erroreNomeUtente() ? 'errore-nomeUtente' : null"
              autocomplete="username"
            />
            @if (erroreNomeUtente()) {
              <p class="messaggio-errore" id="errore-nomeUtente">{{ erroreNomeUtente() }}</p>
            }
          </div>

          <div class="campo">
            <label for="password">{{ testi.etichettaPassword }}</label>
            <input
              id="password"
              type="password"
              formControlName="password"
              [attr.aria-invalid]="errorePassword() ? 'true' : null"
              [attr.aria-describedby]="errorePassword() ? 'errore-password' : null"
              autocomplete="current-password"
            />
            @if (errorePassword()) {
              <p class="messaggio-errore" id="errore-password">{{ errorePassword() }}</p>
            }
          </div>

          <button type="submit" class="btn-principale" [disabled]="inCorso()">{{ testi.pulsanteAccedi }}</button>
        </form>
      </div>
    </div>
  `,
})
export class LoginPageComponent {
  protected readonly nomeApp = NOME_APP;
  protected readonly testi = login;
  protected readonly inCorso = signal(false);
  protected readonly erroreNomeUtente = signal<string | null>(null);
  protected readonly errorePassword = signal<string | null>(null);
  protected readonly erroreGenerale = signal<string | null>(null);

  protected readonly form: ReturnType<FormBuilder['group']>;

  constructor(
    private readonly fb: FormBuilder,
    private readonly api: ApiService,
    private readonly session: SessionService,
    private readonly router: Router,
  ) {
    this.form = this.fb.group({
      nomeUtente: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  accedi(): void {
    this.erroreNomeUtente.set(null);
    this.errorePassword.set(null);
    this.erroreGenerale.set(null);
    this.inCorso.set(true);
    const { nomeUtente, password } = this.form.getRawValue();
    this.api.accesso(nomeUtente ?? '', password ?? '').subscribe({
      next: (risposta) => {
        this.inCorso.set(false);
        this.form.patchValue({ password: '' });
        this.session.pendingUser = risposta.nomeUtente;
        this.router.navigateByUrl('/conferma');
      },
      error: (errore: ApiError) => {
        this.inCorso.set(false);
        this.form.patchValue({ password: '' });
        this.applicaErrori(errore);
      },
    });
  }

  private applicaErrori(errore: ApiError): void {
    if (errore.codice !== 'VALIDAZIONE') {
      this.erroreGenerale.set(erroreGenerico(errore.codice));
      return;
    }
    for (const dettaglio of errore.dettagli) {
      const messaggio = erroreAccesso(dettaglio);
      if (dettaglio.campo === 'nomeUtente') {
        this.erroreNomeUtente.set(messaggio);
      } else if (dettaglio.campo === 'password') {
        this.errorePassword.set(messaggio);
      }
    }
  }
}
