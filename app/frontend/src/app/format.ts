// Formattazione dei numeri e delle date. Nessun calcolo di business: solo presentazione
// dei valori già calcolati dal backend.

/** Inserisce il punto delle migliaia in un numero intero (positivo o negativo). */
function conPuntoMigliaia(valore: number): string {
  const negativo = valore < 0;
  const cifre = Math.abs(Math.round(valore)).toString();
  const conPunti = cifre.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
  return negativo ? `-${conPunti}` : conPunti;
}

/** Importo in euro, arrotondato all'unità, col punto delle migliaia: "12.000 €". */
export function euro(valore: number): string {
  return `${conPuntoMigliaia(valore)} €`;
}

/** Numero intero con punto delle migliaia, senza unità di misura: "28.000". */
export function numero(valore: number): string {
  return conPuntoMigliaia(valore);
}

/** Percentuale intera: "24%". */
export function percentuale(valore: number): string {
  return `${Math.round(valore)}%`;
}

/** Data e ora in formato italiano: "05/10/2026 alle 10:15". */
export function dataOra(iso: string): string {
  // Formato atteso in ingresso: "yyyy-MM-ddTHH:mm:ss"
  const m = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})/.exec(iso);
  if (!m) {
    return iso;
  }
  const [, anno, mese, giorno, ora, minuti] = m;
  return `${giorno}/${mese}/${anno} alle ${ora}:${minuti}`;
}

/** Sostituisce i segnaposto {D}, {P}, {X}, {Y}, {Z}, {N}, {MAX} in un modello di frase. */
export function riempi(modello: string, valori: Record<string, string | number>): string {
  return modello.replace(/\{(\w+)\}/g, (match, chiave) => {
    const valore = valori[chiave];
    return valore === undefined ? match : String(valore);
  });
}
