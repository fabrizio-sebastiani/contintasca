import { Component, input, output } from '@angular/core';
import { VoceAnalisi } from '../../api.models';
import { euro } from '../../format';
import { nomeVoce, analisi } from '../../testi';

/** Ragnatela SVG scritta a mano (RB-15, RB-16). Due forme sovrapposte: "La tua spesa"
 * (linea continua) e "Spesa campione" (tratteggiata), stessa scala su tutti i rami. */
@Component({
  selector: 'app-radar-chart',
  standalone: true,
  template: `
    <figure style="margin: 0;">
      <svg viewBox="0 0 520 440" role="img" [attr.aria-label]="'Ragnatela della spesa'" width="100%" height="auto">
        <!-- Griglia: 4 pentagoni concentrici -->
        @for (k of [1, 2, 3, 4]; track k) {
          <polygon [attr.points]="anelloGriglia(k)" fill="none" stroke="#e3dcee" stroke-width="1" />
        }

        <!-- Poligono spesa campione (grigio tratteggiato) -->
        <polygon
          [attr.points]="poligono('spesaCampione')"
          fill="none"
          stroke="#9b94a6"
          stroke-width="2.5"
          stroke-dasharray="8 6"
        />
        <!-- Poligono la tua spesa (viola pieno semitrasparente) -->
        <polygon
          [attr.points]="poligono('spesaCorrente')"
          fill="rgba(117, 0, 192, 0.16)"
          stroke="#7500c0"
          stroke-width="3"
        />

        <!-- Rami -->
        @for (voce of voci(); track voce.voce; let i = $index) {
          <g
            tabindex="0"
            role="button"
            [attr.aria-pressed]="voceMostrata() === voce.voce"
            [attr.aria-label]="etichettaRamo(voce)"
            (mouseenter)="seleziona(voce.voce)"
            (focus)="seleziona(voce.voce)"
            (click)="seleziona(voce.voce)"
            (keydown.enter)="onKey($event, voce.voce)"
            (keydown.space)="onKey($event, voce.voce)"
            style="cursor: pointer; outline-offset: 4px;"
          >
            <!-- area di clic larga e trasparente -->
            <line
              [attr.x1]="cx"
              [attr.y1]="cy"
              [attr.x2]="assePunto(i).x"
              [attr.y2]="assePunto(i).y"
              stroke="transparent"
              stroke-width="28"
            />
            <line
              [attr.x1]="cx"
              [attr.y1]="cy"
              [attr.x2]="assePunto(i).x"
              [attr.y2]="assePunto(i).y"
              [attr.stroke]="voceMostrata() === voce.voce ? '#7500c0' : '#d9d3e3'"
              [attr.stroke-width]="voceMostrata() === voce.voce ? 3 : 1"
            />
            <circle
              [attr.cx]="puntoValore(i, voce.spesaCorrente).x"
              [attr.cy]="puntoValore(i, voce.spesaCorrente).y"
              [attr.r]="voceMostrata() === voce.voce ? 7 : 5"
              fill="#7500c0"
            />
            <circle
              [attr.cx]="puntoValore(i, voce.spesaCampione).x"
              [attr.cy]="puntoValore(i, voce.spesaCampione).y"
              [attr.r]="voceMostrata() === voce.voce ? 7 : 5"
              fill="#9b94a6"
            />
            <text
              [attr.x]="puntoEtichetta(i).x"
              [attr.y]="puntoEtichetta(i).y"
              [attr.text-anchor]="ancoraggio(i)"
              [attr.font-weight]="voceMostrata() === voce.voce ? 'bold' : 'normal'"
              [attr.fill]="voceMostrata() === voce.voce ? '#7500c0' : '#1f1a24'"
              font-size="15"
            >
              {{ nomeVoceDi(voce.voce) }}
            </text>
            <text
              [attr.x]="puntoEtichetta(i).x"
              [attr.y]="puntoEtichetta(i).y + 18"
              [attr.text-anchor]="ancoraggio(i)"
              font-size="13"
            >
              {{ euroFmt(voce.spesaCorrente) }} / {{ euroFmt(voce.spesaCampione) }}
            </text>
          </g>
        }
      </svg>
      <figcaption style="display: flex; flex-wrap: wrap; gap: 20px; justify-content: center; margin-top: 8px;">
        <span style="display: inline-flex; align-items: center; gap: 8px; font-size: 14px; font-weight: 600;">
          <svg width="24" height="8" aria-hidden="true"><line x1="0" y1="4" x2="24" y2="4" stroke="#7500c0" stroke-width="3" /></svg>
          {{ testi.legendaCorrente }}
        </span>
        <span style="display: inline-flex; align-items: center; gap: 8px; font-size: 14px; font-weight: 600; color: var(--colore-testo-secondario);">
          <svg width="24" height="8" aria-hidden="true"
            ><line x1="0" y1="4" x2="24" y2="4" stroke="#9b94a6" stroke-width="3" stroke-dasharray="6 4"
          /></svg>
          {{ testi.legendaCampione }}
        </span>
      </figcaption>
    </figure>
  `,
})
export class RadarChartComponent {
  readonly voci = input<VoceAnalisi[]>([]);
  readonly scalaMassima = input<number>(0);
  readonly voceMostrata = input<string>('TOTALE');
  readonly selezionato = output<string>();

  protected readonly testi = analisi;
  protected readonly cx = 260;
  protected readonly cy = 220;
  protected readonly raggio = 150;

  private scala(): number {
    return this.scalaMassima() || 1;
  }

  private angoloRad(i: number): number {
    return ((-90 + i * 72) * Math.PI) / 180;
  }

  protected puntoRaggio(i: number, raggio: number): { x: number; y: number } {
    const a = this.angoloRad(i);
    return { x: this.cx + raggio * Math.cos(a), y: this.cy + raggio * Math.sin(a) };
  }

  protected assePunto(i: number): { x: number; y: number } {
    return this.puntoRaggio(i, this.raggio);
  }

  protected puntoEtichetta(i: number): { x: number; y: number } {
    return this.puntoRaggio(i, this.raggio + 46);
  }

  protected puntoValore(i: number, valore: number): { x: number; y: number } {
    const ratio = Math.min(1, valore / this.scala());
    return this.puntoRaggio(i, this.raggio * ratio);
  }

  protected ancoraggio(i: number): string {
    const p = this.assePunto(i);
    if (p.x < this.cx - 5) {
      return 'end';
    }
    if (p.x > this.cx + 5) {
      return 'start';
    }
    return 'middle';
  }

  protected anelloGriglia(k: number): string {
    const raggio = (this.raggio * k) / 4;
    return [0, 1, 2, 3, 4]
      .map((i) => {
        const p = this.puntoRaggio(i, raggio);
        return `${p.x},${p.y}`;
      })
      .join(' ');
  }

  protected poligono(chiave: 'spesaCorrente' | 'spesaCampione'): string {
    return this.voci()
      .map((v, i) => {
        const p = this.puntoValore(i, v[chiave]);
        return `${p.x},${p.y}`;
      })
      .join(' ');
  }

  protected nomeVoceDi(codice: string): string {
    return nomeVoce(codice);
  }

  protected euroFmt(valore: number): string {
    return euro(valore);
  }

  protected etichettaRamo(voce: VoceAnalisi): string {
    return `${nomeVoce(voce.voce)}: ${euro(voce.spesaCorrente)}, campione ${euro(voce.spesaCampione)}`;
  }

  protected onKey(evento: Event, codice: string): void {
    evento.preventDefault();
    this.seleziona(codice);
  }

  protected seleziona(codice: string): void {
    this.selezionato.emit(codice);
  }
}
