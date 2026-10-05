// Interfacce copiate dal contratto API (agents/docs/contratto-api.md). Non contengono logica.

export interface ErrorDetail {
  campo: string;
  codice: string;
  minimo: number | null;
  massimo: number | null;
}

export interface ApiError {
  codice: string;
  messaggio: string;
  dettagli: ErrorDetail[];
}

export interface AccessoRequest {
  nomeUtente: string;
  password: string;
}

export interface AccessoResponse {
  nomeUtente: string;
}

export interface DatiAttuali {
  caricato: boolean;
  nomeFile: string | null;
  caricatoIl: string | null;
  numeroDomande: number;
  numeroFasce: number;
}

export interface ScartoRiga {
  riga: number;
  codice: string;
}

export interface CaricamentoDatiResponse {
  righeLette: number;
  righeImportate: number;
  righeScartate: number;
  scarti: ScartoRiga[];
  datiAttuali: DatiAttuali;
}

export type TipoDomanda = 'SCELTA' | 'NUMERO';

export interface OpzioneDomanda {
  codice: string;
  testo: string;
}

export interface Domanda {
  codice: string;
  tipo: TipoDomanda;
  etichetta: string;
  testo: string;
  spiegazione: string | null;
  opzioni: OpzioneDomanda[];
  minimo: number | null;
  massimo: number | null;
}

export type StatoProfilo = 'COMPLETO' | 'NON_PIU_COMPLETO' | 'ASSENTE';

export interface ProfiloResponse {
  statoProfilo: StatoProfilo;
  domande: Domanda[];
  voci: string[];
  risposte: Record<string, string>;
  importi: Record<string, number> | null;
  salvatoIl: string | null;
}

export interface ProfiloSalvaRequest {
  risposte: Record<string, string | null>;
  importi: Record<string, string | null>;
}

export interface ProfiloSalvaResponse {
  statoProfilo: StatoProfilo;
  salvatoIl: string;
}

export type EsitoConfronto = 'SOPRA' | 'SOTTO' | 'IN_LINEA';

export interface RispostaAnalisi {
  etichetta: string;
  tipo: TipoDomanda;
  testoOpzione: string | null;
  numero: number | null;
}

export interface VoceAnalisi {
  voce: string;
  spesaCorrente: number;
  spesaCampione: number;
  differenza: number;
  differenzaAssoluta: number;
  percentuale: number | null;
  esito: EsitoConfronto;
  fraseCifra: string;
}

export interface Fascia {
  codice: string;
  minimo: number;
  massimo: number;
}

export interface AnalisiResponse {
  punteggio: number;
  fascia: Fascia;
  approssimato: boolean;
  risposte: RispostaAnalisi[];
  voci: VoceAnalisi[];
  totale: VoceAnalisi;
  scalaMassima: number;
}
