import { Component, HostListener, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../api.service';
import { SessionService } from '../../session.service';
import { NoDataComponent } from '../../shared/no-data.component';
import { RadarChartComponent } from './radar-chart.component';
import { ApiError, AnalisiResponse, VoceAnalisi } from '../../api.models';
import { euro, numero } from '../../format';
import {
  analisi,
  nomeVoce,
  tabellaA,
  tabellaC,
  fraseCifra,
  fraseConChiTiConfronti,
  avvisoApprossimato,
  rigaRisposta,
  paroleUtili,
  erroreGenerico,
} from '../../testi';

@Component({
  selector: 'app-analysis-page',
  standalone: true,
  imports: [NoDataComponent, RadarChartComponent],
  template: `
    @if (nessunDato()) {
      <app-no-data />
    } @else if (erroreGenerale()) {
      <div class="contenitore">
        <p class="messaggio-errore" role="alert">{{ erroreGenerale() }}</p>
      </div>
    } @else if (dati()) {
      <div class="contenitore layout-analisi">
        <section class="colonna-ragnatela">
          <app-radar-chart
            [voci]="dati()!.voci"
            [scalaMassima]="dati()!.scalaMassima"
            [voceMostrata]="voceMostrata()"
            (selezionato)="onSeleziona($event)"
          />
        </section>

        <section class="colonna-box" aria-live="polite">
          <h1>{{ titoloBox() }}</h1>
          <p>{{ testi.etichettaSpesaCorrente }}: {{ euroFmt(voceCorrente()!.spesaCorrente) }}</p>
          <p>{{ testi.etichettaSpesaCampione }}: {{ euroFmt(voceCorrente()!.spesaCampione) }}</p>
          <h2>{{ testi.titoloDaSapere }}</h2>
          <ul>
            <li>{{ fraseA() }}</li>
            <li>{{ fraseB() }}</li>
            <li>{{ fraseC() }}</li>
          </ul>
          <button type="button" class="btn-secondario" (click)="vediTotali()">
            {{ testi.pulsanteVediTotali }}
          </button>
        </section>

        <section class="colonna-sotto">
          <h2>{{ testi.titoloConfronto }}</h2>
          <p>{{ fraseConfronto() }}</p>
          @if (dati()!.approssimato) {
            <p class="avviso" role="alert">{{ fraseApprossimato() }}</p>
          }
          <ul>
            @for (r of dati()!.risposte; track $index) {
              <li>{{ rigaRispostaDi(r) }}</li>
            }
          </ul>

          <h2>{{ testi.titoloParoleUtili }}</h2>
          <dl>
            @for (p of paroleUtili; track p.termine) {
              <dt>{{ p.termine }}</dt>
              <dd>{{ p.definizione }}</dd>
            }
          </dl>

          <p>{{ testi.fraseMedia }}</p>
          <p class="disclaimer">{{ testi.disclaimer }}</p>
          <p class="disclaimer">{{ testi.fonte }}</p>
        </section>
      </div>
    }
  `,
  styles: [
    `
      .layout-analisi {
        display: grid;
        grid-template-columns: 1fr;
        gap: 24px;
      }
      @media (min-width: 900px) {
        .layout-analisi {
          grid-template-columns: 1fr 1fr;
        }
        .colonna-sotto {
          grid-column: 1 / span 2;
        }
      }
    `,
  ],
})
export class AnalysisPageComponent implements OnInit {
  protected readonly testi = analisi;
  protected readonly paroleUtili = paroleUtili;
  protected readonly nessunDato = signal(false);
  protected readonly erroreGenerale = signal<string | null>(null);
  protected readonly dati = signal<AnalisiResponse | null>(null);
  protected readonly voceMostrata = signal('TOTALE');

  constructor(
    private readonly api: ApiService,
    private readonly session: SessionService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    const nome = this.session.nomeUtente();
    if (!nome) {
      return;
    }
    this.api.getAnalisi(nome).subscribe({
      next: (risposta) => this.dati.set(risposta),
      error: (errore: ApiError) => this.gestisciErrore(errore),
    });
  }

  private gestisciErrore(errore: ApiError): void {
    switch (errore.codice) {
      case 'NESSUN_DATO':
        this.nessunDato.set(true);
        break;
      case 'PROFILO_ASSENTE':
        this.router.navigateByUrl('/profilo?avviso=compila');
        break;
      case 'PROFILO_NON_PIU_COMPLETO':
        this.router.navigateByUrl('/profilo');
        break;
      default:
        this.erroreGenerale.set(erroreGenerico(errore.codice));
    }
  }

  // RB-16: Esc riporta la voce mostrata al totale (se il menu è chiuso: vedi ShellComponent).
  @HostListener('window:keydown.escape')
  onEscape(): void {
    this.voceMostrata.set('TOTALE');
  }

  onSeleziona(codice: string): void {
    this.voceMostrata.set(codice);
  }

  vediTotali(): void {
    this.voceMostrata.set('TOTALE');
  }

  protected voceCorrente(): VoceAnalisi | null {
    const d = this.dati();
    if (!d) {
      return null;
    }
    if (this.voceMostrata() === 'TOTALE') {
      return d.totale;
    }
    return d.voci.find((v) => v.voce === this.voceMostrata()) ?? d.totale;
  }

  protected titoloBox(): string {
    return this.voceMostrata() === 'TOTALE' ? this.testi.titoloTotale : nomeVoce(this.voceMostrata());
  }

  protected fraseA(): string {
    return tabellaA[this.voceMostrata()] ?? '';
  }

  protected fraseB(): string {
    const v = this.voceCorrente();
    if (!v) {
      return '';
    }
    return fraseCifra(v.fraseCifra, this.voceMostrata(), v.differenzaAssoluta, v.percentuale);
  }

  protected fraseC(): string {
    const v = this.voceCorrente();
    if (!v) {
      return '';
    }
    return tabellaC[this.voceMostrata()]?.[v.esito] ?? '';
  }

  protected fraseConfronto(): string {
    const d = this.dati();
    if (!d) {
      return '';
    }
    return fraseConChiTiConfronti(d.fascia.minimo, d.fascia.massimo, d.punteggio);
  }

  protected fraseApprossimato(): string {
    const d = this.dati();
    if (!d) {
      return '';
    }
    return avvisoApprossimato(d.punteggio, d.fascia.minimo, d.fascia.massimo);
  }

  protected rigaRispostaDi(r: { etichetta: string; tipo: string; testoOpzione: string | null; numero: number | null }): string {
    const valore = r.tipo === 'SCELTA' ? (r.testoOpzione ?? '') : numero(r.numero ?? 0);
    return rigaRisposta(r.etichetta, valore);
  }

  protected euroFmt(valore: number): string {
    return euro(valore);
  }
}
