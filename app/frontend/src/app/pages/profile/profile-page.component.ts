import { Component, ElementRef, OnInit, signal, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../api.service';
import { SessionService } from '../../session.service';
import { NoDataComponent } from '../../shared/no-data.component';
import { ApiError, Domanda, ProfiloResponse } from '../../api.models';
import { nomeVoce, tabellaA, profilo, erroreProfiloCampo, erroreGenerico } from '../../testi';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [ReactiveFormsModule, NoDataComponent],
  template: `
    @if (nessunDato()) {
      <app-no-data />
    } @else if (pronto()) {
      <div class="contenitore">
       <div class="card">
        <h1>{{ testi.titoloMenuProfilo }}</h1>

        @if (avvisoNonPiuCompleto()) {
          <p class="avviso" role="alert">{{ testi.avvisoNonPiuCompleto }}</p>
        }
        @if (avvisoCompilaPrima()) {
          <p class="avviso" role="alert">{{ testi.avvisoCompilaPrima }}</p>
        }
        @if (riepilogoErrori() > 0) {
          <p class="riepilogo-errori" role="alert" tabindex="-1" #riepilogo>
            {{ testi.riepilogoErrori(riepilogoErrori()) }}
          </p>
        }
        @if (erroreGenerale()) {
          <p class="messaggio-errore" role="alert">{{ erroreGenerale() }}</p>
        }

        <form [formGroup]="form" (ngSubmit)="salva()" novalidate>
          <h2>{{ testi.titoloParteA }}</h2>
          @for (domanda of domande(); track domanda.codice) {
            <div class="campo">
              <label [for]="'d-' + domanda.codice">{{ domanda.testo }}</label>
              @if (domanda.spiegazione) {
                <p class="aiuto">{{ domanda.spiegazione }}</p>
              }
              @if (domanda.tipo === 'SCELTA' && domanda.opzioni.length <= 4) {
                <div class="radio-opzioni" [attr.aria-describedby]="erroreRisposta(domanda.codice) ? 'err-d-' + domanda.codice : null">
                  @for (opzione of domanda.opzioni; track opzione.codice) {
                    <label>
                      <input
                        type="radio"
                        [id]="'d-' + domanda.codice + '-' + opzione.codice"
                        [formControlName]="'d_' + domanda.codice"
                        [value]="opzione.codice"
                      />
                      {{ opzione.testo }}
                    </label>
                  }
                </div>
              } @else if (domanda.tipo === 'SCELTA') {
                <select
                  [id]="'d-' + domanda.codice"
                  [formControlName]="'d_' + domanda.codice"
                  [attr.aria-invalid]="erroreRisposta(domanda.codice) ? 'true' : null"
                  [attr.aria-describedby]="erroreRisposta(domanda.codice) ? 'err-d-' + domanda.codice : null"
                >
                  <option value="" disabled>-</option>
                  @for (opzione of domanda.opzioni; track opzione.codice) {
                    <option [value]="opzione.codice">{{ opzione.testo }}</option>
                  }
                </select>
              } @else {
                <input
                  type="text"
                  inputmode="numeric"
                  [id]="'d-' + domanda.codice"
                  [formControlName]="'d_' + domanda.codice"
                  [attr.aria-invalid]="erroreRisposta(domanda.codice) ? 'true' : null"
                  [attr.aria-describedby]="erroreRisposta(domanda.codice) ? 'err-d-' + domanda.codice : null"
                />
              }
              @if (erroreRisposta(domanda.codice)) {
                <p class="messaggio-errore" [id]="'err-d-' + domanda.codice">{{ erroreRisposta(domanda.codice) }}</p>
              }
            </div>
          }

          <h2>{{ testi.titoloParteB }}</h2>
          @for (voce of voci(); track voce) {
            <div class="campo">
              <label [for]="'v-' + voce">{{ nomeVoceDi(voce) }}</label>
              <p class="aiuto">{{ cosaIncludeDi(voce) }}</p>
              <p class="aiuto">{{ testi.aiutoImporto }}</p>
              <input
                type="text"
                inputmode="numeric"
                [id]="'v-' + voce"
                [formControlName]="'v_' + voce"
                [attr.aria-invalid]="erroreImporto(voce) ? 'true' : null"
                [attr.aria-describedby]="erroreImporto(voce) ? 'err-v-' + voce : null"
              />
              @if (erroreImporto(voce)) {
                <p class="messaggio-errore" [id]="'err-v-' + voce">{{ erroreImporto(voce) }}</p>
              }
            </div>
          }

          <button type="submit" class="btn-principale" [disabled]="inCorso()">{{ testi.pulsanteSalva }}</button>
        </form>
       </div>
      </div>
    }
  `,
})
export class ProfilePageComponent implements OnInit {
  protected readonly testi = profilo;
  protected readonly nessunDato = signal(false);
  protected readonly pronto = signal(false);
  protected readonly inCorso = signal(false);
  protected readonly domande = signal<Domanda[]>([]);
  protected readonly voci = signal<string[]>([]);
  protected readonly avvisoNonPiuCompleto = signal(false);
  protected readonly avvisoCompilaPrima = signal(false);
  protected readonly erroreGenerale = signal<string | null>(null);
  protected readonly erroriRisposte = signal<Record<string, string>>({});
  protected readonly erroriImporti = signal<Record<string, string>>({});
  protected readonly riepilogoErrori = signal(0);

  @ViewChild('riepilogo') riepilogoEl?: ElementRef<HTMLElement>;

  protected form: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly api: ApiService,
    private readonly session: SessionService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {
    this.form = this.fb.group({});
  }

  ngOnInit(): void {
    const nome = this.session.nomeUtente();
    if (!nome) {
      return;
    }
    this.api.getProfilo(nome).subscribe({
      next: (risposta) => this.inizializza(risposta),
      error: (errore: ApiError) => {
        if (errore.codice === 'NESSUN_DATO') {
          this.nessunDato.set(true);
        } else {
          this.erroreGenerale.set(erroreGenerico(errore.codice));
          this.pronto.set(true);
        }
      },
    });
  }

  private inizializza(risposta: ProfiloResponse): void {
    this.domande.set(risposta.domande);
    this.voci.set(risposta.voci);
    this.avvisoNonPiuCompleto.set(risposta.statoProfilo === 'NON_PIU_COMPLETO');
    this.avvisoCompilaPrima.set(
      risposta.statoProfilo === 'ASSENTE' && this.route.snapshot.queryParamMap.get('avviso') === 'compila',
    );

    const controlli: Record<string, unknown> = {};
    for (const domanda of risposta.domande) {
      controlli['d_' + domanda.codice] = [risposta.risposte[domanda.codice] ?? ''];
    }
    for (const voce of risposta.voci) {
      const importo = risposta.importi?.[voce];
      controlli['v_' + voce] = [importo !== undefined && importo !== null ? String(importo) : ''];
    }
    this.form = this.fb.group(controlli);
    this.pronto.set(true);
  }

  protected nomeVoceDi(codice: string): string {
    return nomeVoce(codice);
  }

  protected cosaIncludeDi(codice: string): string {
    return tabellaA[codice] ?? '';
  }

  protected erroreRisposta(codice: string): string | null {
    return this.erroriRisposte()[codice] ?? null;
  }

  protected erroreImporto(codice: string): string | null {
    return this.erroriImporti()[codice] ?? null;
  }

  salva(): void {
    const nome = this.session.nomeUtente();
    if (!nome) {
      return;
    }
    this.erroriRisposte.set({});
    this.erroriImporti.set({});
    this.riepilogoErrori.set(0);
    this.erroreGenerale.set(null);
    this.inCorso.set(true);

    const valori = this.form.getRawValue() as Record<string, string>;
    const risposte: Record<string, string | null> = {};
    for (const domanda of this.domande()) {
      risposte[domanda.codice] = valori['d_' + domanda.codice] || null;
    }
    const importi: Record<string, string | null> = {};
    for (const voce of this.voci()) {
      importi[voce] = valori['v_' + voce] || null;
    }

    this.api.salvaProfilo(nome, { risposte, importi }).subscribe({
      next: () => {
        this.inCorso.set(false);
        this.router.navigateByUrl('/analisi');
      },
      error: (errore: ApiError) => {
        this.inCorso.set(false);
        if (errore.codice === 'VALIDAZIONE') {
          const erroriR: Record<string, string> = {};
          const erroriI: Record<string, string> = {};
          for (const dettaglio of errore.dettagli) {
            const messaggio = erroreProfiloCampo(dettaglio);
            if (dettaglio.campo.startsWith('risposte.')) {
              erroriR[dettaglio.campo.substring('risposte.'.length)] = messaggio;
            } else if (dettaglio.campo.startsWith('importi.')) {
              erroriI[dettaglio.campo.substring('importi.'.length)] = messaggio;
            }
          }
          this.erroriRisposte.set(erroriR);
          this.erroriImporti.set(erroriI);
          this.riepilogoErrori.set(errore.dettagli.length);
          queueMicrotask(() => this.riepilogoEl?.nativeElement.focus());
        } else {
          this.erroreGenerale.set(erroreGenerico(errore.codice));
        }
      },
    });
  }
}
