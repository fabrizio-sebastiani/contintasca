import { Component, OnInit, signal } from '@angular/core';
import { ApiService } from '../../api.service';
import { ApiError, CaricamentoDatiResponse, DatiAttuali } from '../../api.models';
import { amministrazione, erroreFile, motivoScarto } from '../../testi';
import { dataOra } from '../../format';

@Component({
  selector: 'app-admin-page',
  standalone: true,
  template: `
    <div class="contenitore">
      <h1>{{ testi.titolo }}</h1>
      <p>{{ testi.testoFisso }}</p>

      <section class="avviso-demo">
        <h2>{{ testi.titoloDatiAttuali }}</h2>
        @if (datiAttuali() && datiAttuali()!.caricato) {
          <ul>
            <li>Nome del file: {{ datiAttuali()!.nomeFile }}</li>
            <li>Caricato il: {{ dataOraFmt(datiAttuali()!.caricatoIl!) }}</li>
            <li>Numero di domande: {{ datiAttuali()!.numeroDomande }}</li>
            <li>Numero di fasce: {{ datiAttuali()!.numeroFasce }}</li>
          </ul>
        } @else {
          <p>{{ testi.nessunDatoCaricato }}</p>
        }
      </section>

      <div class="campo">
        <label for="file-dati">{{ testi.etichettaFile }}</label>
        <input id="file-dati" type="file" accept=".csv" (change)="selezionaFile($event)" />
      </div>

      @if (erroreCaricamento()) {
        <p class="messaggio-errore" role="alert">{{ erroreCaricamento() }}</p>
      }

      <button type="button" class="btn-principale" (click)="carica()" [disabled]="inCorso()">
        {{ testi.pulsanteCarica }}
      </button>

      @if (esito()) {
        <section style="margin-top: 24px;">
          <p><strong>{{ testi.caricamentoCompletato }}</strong></p>
          <p>{{ testi.righeLette(esito()!.righeLette) }}</p>
          <p>{{ testi.righeImportate(esito()!.righeImportate) }}</p>
          <p>{{ testi.righeScartate(esito()!.righeScartate) }}</p>
          @if (esito()!.scarti.length > 0) {
            <table>
              <thead>
                <tr>
                  <th>{{ testi.intestazioneRiga }}</th>
                  <th>{{ testi.intestazioneMotivo }}</th>
                </tr>
              </thead>
              <tbody>
                @for (scarto of esito()!.scarti; track scarto.riga) {
                  <tr>
                    <td>{{ scarto.riga }}</td>
                    <td>{{ motivo(scarto.codice) }}</td>
                  </tr>
                }
              </tbody>
            </table>
          }
        </section>
      }
    </div>
  `,
})
export class AdminPageComponent implements OnInit {
  protected readonly testi = amministrazione;
  protected readonly datiAttuali = signal<DatiAttuali | null>(null);
  protected readonly esito = signal<CaricamentoDatiResponse | null>(null);
  protected readonly erroreCaricamento = signal<string | null>(null);
  protected readonly inCorso = signal(false);
  private fileSelezionato: File | null = null;

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.api.getDatiAttuali().subscribe({
      next: (dati) => this.datiAttuali.set(dati),
      error: () => this.datiAttuali.set(null),
    });
  }

  selezionaFile(evento: Event): void {
    const input = evento.target as HTMLInputElement;
    this.fileSelezionato = input.files && input.files.length > 0 ? input.files[0] : null;
  }

  carica(): void {
    this.erroreCaricamento.set(null);
    this.esito.set(null);
    if (!this.fileSelezionato) {
      this.erroreCaricamento.set(this.testi.erroreNessunFileScelto);
      return;
    }
    this.inCorso.set(true);
    this.api.caricaDati(this.fileSelezionato).subscribe({
      next: (risposta) => {
        this.inCorso.set(false);
        this.esito.set(risposta);
        this.datiAttuali.set(risposta.datiAttuali);
      },
      error: (errore: ApiError) => {
        this.inCorso.set(false);
        this.erroreCaricamento.set(erroreFile(errore.codice));
      },
    });
  }

  protected motivo(codice: string): string {
    return motivoScarto(codice);
  }

  protected dataOraFmt(iso: string): string {
    return dataOra(iso);
  }
}
