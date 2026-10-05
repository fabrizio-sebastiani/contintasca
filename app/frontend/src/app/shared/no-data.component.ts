import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { noData } from '../testi';

/** RB-25: mostrata da S-03 e S-04 quando non ci sono ancora dati caricati. */
@Component({
  selector: 'app-no-data',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="contenitore">
      <p class="avviso">
        {{ testi.messaggio }}
        <a routerLink="/amministrazione">{{ testi.collegamento }}</a>
      </p>
    </div>
  `,
})
export class NoDataComponent {
  protected readonly testi = noData;
}
