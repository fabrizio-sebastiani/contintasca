// Tutti i testi visibili dell'app "ContiInTasca".
// Regole: agents/skills/linguaggio-semplice/SKILL.md. Nessun componente scrive frasi nel template:
// ogni testo visibile viene letto da qui.
import { ErrorDetail } from './api.models';
import { riempi } from './format';

export const NOME_APP = 'ContiInTasca';

// ---------------------------------------------------------------------------
// Nomi delle voci di spesa (sezione 1 del contratto, ordine RB-15)
// ---------------------------------------------------------------------------
export const NOME_VOCE: Record<string, string> = {
  CASA: 'Casa',
  SPORT_TEMPO_LIBERO: 'Sport e tempo libero',
  AUTO_MOBILITA: 'Auto e mobilità',
  UTENZE: 'Utenze',
  SPESA: 'Spesa',
  TOTALE: 'Totale',
};

export function nomeVoce(codice: string): string {
  return NOME_VOCE[codice] ?? codice;
}

// ---------------------------------------------------------------------------
// S-01 Login
// ---------------------------------------------------------------------------
export const login = {
  frasePresentazione: 'Confronta le tue spese di un anno con quelle di persone con un profilo come il tuo.',
  avvisoDemo: 'Accesso dimostrativo: puoi usare qualsiasi nome utente e password.',
  etichettaNomeUtente: 'Nome utente',
  etichettaPassword: 'Password',
  pulsanteAccedi: 'Accedi',
};

/** Messaggi di errore di S-01, accanto al campo, in tre parti (dal dettaglio del backend). */
export function erroreAccesso(dettaglio: ErrorDetail): string {
  if (dettaglio.campo === 'nomeUtente') {
    if (dettaglio.codice === 'CAMPO_MANCANTE') {
      return 'Manca il nome utente. Serve per entrare nell\'app. Puoi scrivere qualsiasi nome utente.';
    }
    return riempi(
      'Il nome utente supera la lunghezza massima. Puoi scrivere al massimo {MASSIMO} caratteri. Correggi il campo nome utente.',
      { MASSIMO: dettaglio.massimo ?? 50 },
    );
  }
  if (dettaglio.campo === 'password') {
    if (dettaglio.codice === 'CAMPO_MANCANTE') {
      return 'Manca la password. Serve per entrare nell\'app. Puoi scrivere qualsiasi password.';
    }
    return riempi(
      'La password supera la lunghezza massima. Puoi scrivere al massimo {MASSIMO} caratteri. Correggi il campo password.',
      { MASSIMO: dettaglio.massimo ?? 100 },
    );
  }
  return erroreGenerico('RICHIESTA_NON_VALIDA');
}

// ---------------------------------------------------------------------------
// S-02 Conferma accesso
// ---------------------------------------------------------------------------
export const conferma = {
  notificaInviata: 'Ti abbiamo inviato una notifica sul telefono.',
  avvisoDemo: 'Accesso dimostrativo: la notifica è simulata e viene accettata da sola.',
  pulsanteAnnulla: 'Annulla',
  attesaInCorso: (secondi: number) => `Accesso confermato tra ${secondi} secondi.`,
};

// ---------------------------------------------------------------------------
// Intestazione e menu hamburger
// ---------------------------------------------------------------------------
export const menu = {
  etichettaPulsante: 'Menu',
  vociProfilo: 'Profilo',
  voceAnalisi: 'Analisi di spesa',
  voceAmministrazione: 'Amministrazione',
  voceEsci: 'Esci',
};

// ---------------------------------------------------------------------------
// S-03 Analisi di spesa
// ---------------------------------------------------------------------------
export const analisi = {
  titoloTotale: 'Totale',
  etichettaSpesaCorrente: 'Spesa corrente',
  etichettaSpesaCampione: 'Spesa campione',
  titoloDaSapere: 'Da sapere',
  pulsanteVediTotali: 'Vedi i totali',
  titoloConfronto: 'Con chi ti confronti',
  titoloParoleUtili: 'Parole utili',
  fraseMedia: 'La media è un riferimento, non un obiettivo: ogni situazione è diversa.',
  disclaimer: 'Questo confronto ha solo scopo informativo ed educativo. Non è un consiglio finanziario.',
  fonte: 'Dati dimostrativi ispirati all\'Indagine ISTAT sulle spese delle famiglie, anno 2024.',
  legendaCorrente: 'La tua spesa',
  legendaCampione: 'Spesa campione',
};

/** RB-18: "Ti confronti con persone con un profilo come il tuo: punteggio del profilo da X a Y. Il tuo punteggio è Z." */
export function fraseConChiTiConfronti(x: number, y: number, z: number): string {
  return riempi(
    'Ti confronti con persone con un profilo come il tuo: punteggio del profilo da {X} a {Y}. Il tuo punteggio è {Z}.',
    { X: x, Y: y, Z: z },
  );
}

/** Formato di una riga di risposta: "<etichetta>: <risposta>". */
export function rigaRisposta(etichetta: string, valore: string): string {
  return `${etichetta}: ${valore}`;
}

/** RB-10: avviso di profilo approssimato. */
export function avvisoApprossimato(z: number, x: number, y: number): string {
  return riempi(
    'Nei dati non c\'è un gruppo con il tuo punteggio ({Z}). Ti confrontiamo con il gruppo più vicino: punteggio da {X} a {Y}.',
    { Z: z, X: x, Y: y },
  );
}

export const paroleUtili: { termine: string; definizione: string }[] = [
  {
    termine: 'Spesa campione',
    definizione:
      'Quanto spendono in media in un anno, secondo i dati dimostrativi, persone con un profilo come il tuo.',
  },
  {
    termine: 'Punteggio del profilo',
    definizione: 'Un numero calcolato dalle tue risposte. Serve a trovare nei dati il gruppo di persone più simile a te.',
  },
  {
    termine: 'In linea con la media',
    definizione: 'La tua spesa è diversa dalla spesa campione di non più del 10%.',
  },
];

// ---------------------------------------------------------------------------
// RB-17 tabella A: "cosa include" per voce
// ---------------------------------------------------------------------------
export const tabellaA: Record<string, string> = {
  CASA: 'Questa voce include affitto o rata del mutuo, spese di condominio e piccole riparazioni.',
  SPORT_TEMPO_LIBERO:
    'Questa voce include palestra, attrezzatura sportiva, cinema, concerti, ristoranti, viaggi e uscite.',
  AUTO_MOBILITA: 'Questa voce include carburante, assicurazione, bollo e manutenzione.',
  UTENZE: 'Questa voce include luce, gas, acqua, rifiuti, internet e telefono.',
  SPESA: 'Questa voce include cibo, bevande e prodotti per la casa e per l\'igiene.',
  TOTALE: 'Il totale è la somma delle cinque voci: Casa, Sport e tempo libero, Auto e mobilità, Utenze, Spesa.',
};

// ---------------------------------------------------------------------------
// RB-17 tabella B: frase con la cifra (fraseCifra). {PREFISSO} = "Per <Voce>" o "In totale".
// ---------------------------------------------------------------------------
const tabellaBModelli: Record<string, string> = {
  SOPRA_PERCENTUALE: '{PREFISSO} spendi {D} € all\'anno in più della media di persone come te. È il {P}% in più.',
  SOPRA_OLTRE_10: '{PREFISSO} spendi {D} € all\'anno in più della media di persone come te. È oltre il 10% in più.',
  SOTTO_PERCENTUALE: '{PREFISSO} spendi {D} € all\'anno in meno della media di persone come te. È il {P}% in meno.',
  SOTTO_OLTRE_10: '{PREFISSO} spendi {D} € all\'anno in meno della media di persone come te. È oltre il 10% in meno.',
  IN_LINEA_PIU: '{PREFISSO2} la tua spesa è in linea con la media di persone come te. La differenza è di {D} € all\'anno in più.',
  IN_LINEA_MENO: '{PREFISSO2} la tua spesa è in linea con la media di persone come te. La differenza è di {D} € all\'anno in meno.',
  IN_LINEA_UGUALE: '{PREFISSO2} la tua spesa è uguale alla media di persone come te.',
  SOPRA_CAMPIONE_ZERO: '{PREFISSO} spendi {D} € all\'anno. La spesa campione per questa voce è 0 €.',
};

/** Prefisso soggetto per la tabella B: "Per <Voce>" (con verbo "spendi") o "In totale". */
function prefissoSpendi(voce: string): string {
  return voce === 'TOTALE' ? 'In totale' : `Per ${nomeVoce(voce)}`;
}

/** Prefisso soggetto per la tabella B con "la tua spesa": "Per <Voce>" o "In totale". */
function prefissoSpesa(voce: string): string {
  return voce === 'TOTALE' ? 'In totale' : `Per ${nomeVoce(voce)}`;
}

/** Costruisce la frase con la cifra (RB-17, tabella B) dal codice `fraseCifra`. */
export function fraseCifra(codice: string, voce: string, d: number, p: number | null): string {
  const modello = tabellaBModelli[codice];
  if (!modello) {
    return '';
  }
  return riempi(modello, {
    PREFISSO: prefissoSpendi(voce),
    PREFISSO2: prefissoSpesa(voce),
    D: d,
    P: p ?? 0,
  });
}

// ---------------------------------------------------------------------------
// RB-17 tabella C: frase-concetto per voce ed esito
// ---------------------------------------------------------------------------
export const tabellaC: Record<string, Record<'SOPRA' | 'IN_LINEA' | 'SOTTO', string>> = {
  CASA: {
    SOPRA: 'Una differenza in questa voce può dipendere dalla città e dalla grandezza della casa.',
    IN_LINEA: 'L\'affitto è una spesa fissa: si ripete ogni mese con lo stesso importo.',
    SOTTO: 'Chi vive in una casa di proprietà senza mutuo paga in questa voce solo condominio e riparazioni.',
  },
  SPORT_TEMPO_LIBERO: {
    SOPRA: 'Questa è una spesa variabile: l\'importo cambia da mese a mese in base alle attività.',
    IN_LINEA: 'Alcune spese di questa voce sono fisse, come un abbonamento; altre cambiano ogni mese.',
    SOTTO: 'In questa voce le abitudini personali contano molto, anche tra persone con lo stesso profilo.',
  },
  AUTO_MOBILITA: {
    SOPRA: 'Una parte di queste spese è fissa (bollo, assicurazione): si ripete ogni anno con lo stesso importo.',
    IN_LINEA: 'Il carburante è una spesa variabile: cambia con i chilometri percorsi e con il prezzo alla pompa.',
    SOTTO: 'Questa voce cambia molto con il numero di auto e ciclomotori e con i chilometri percorsi.',
  },
  UTENZE: {
    SOPRA: 'Le bollette di luce e gas cambiano con le stagioni e con il prezzo dell\'energia.',
    IN_LINEA: 'Una bolletta ha una parte fissa, uguale ogni mese, e una parte che cambia con i consumi.',
    SOTTO: 'Questa voce cambia con il numero di persone in casa, la grandezza della casa e il riscaldamento.',
  },
  SPESA: {
    SOPRA: 'Questa voce cambia con il numero di pasti fatti in casa e con i prezzi della zona.',
    IN_LINEA: 'La spesa per il cibo è variabile: l\'importo cambia da settimana a settimana.',
    SOTTO: 'Chi mangia spesso fuori casa ha una parte del cibo nella voce Sport e tempo libero.',
  },
  TOTALE: {
    SOPRA: 'Una differenza nel totale può venire da una sola voce o da più voci insieme.',
    IN_LINEA: 'In linea con la media significa che la differenza è entro il 10% della spesa campione.',
    SOTTO: 'Una differenza nel totale può venire da una sola voce o da più voci insieme.',
  },
};

// ---------------------------------------------------------------------------
// S-04 Profilo
// ---------------------------------------------------------------------------
export const profilo = {
  titoloMenuProfilo: 'Profilo',
  titoloParteA: 'Le tue informazioni',
  titoloParteB: 'Quanto spendi in un anno',
  aiutoImporto: 'Scrivi solo cifre. Esempio: 12000.',
  pulsanteSalva: 'Salva e vedi l\'analisi',
  avvisoNonPiuCompleto: 'Le domande del profilo sono cambiate. Controlla le tue risposte.',
  avvisoCompilaPrima: 'Per vedere l\'analisi di spesa compila prima il tuo profilo.',
  riepilogoErrori: (n: number) => (n === 1 ? 'C\'è 1 campo da correggere.' : `Ci sono ${n} campi da correggere.`),
};

/** Messaggi di errore di S-04 (dettagli di validazione), in tre parti. */
export function erroreProfiloCampo(dettaglio: ErrorDetail): string {
  switch (dettaglio.codice) {
    case 'RISPOSTA_MANCANTE':
    case 'IMPORTO_MANCANTE':
      return 'Manca la risposta. Serve per trovare persone con un profilo come il tuo. Scegli una risposta o scrivi un numero.';
    case 'NUMERO_NON_VALIDO':
      return riempi(
        'Il valore non è valido. Per questa domanda si accettano numeri interi da {MINIMO} a {MASSIMO}. Correggi il campo.',
        { MINIMO: dettaglio.minimo ?? '', MASSIMO: dettaglio.massimo ?? '' },
      );
    case 'IMPORTO_NEGATIVO':
      return 'L\'importo non è valido. Una spesa non può essere sotto zero. Scrivi un numero da 0 a 999999.';
    case 'IMPORTO_NON_CIFRE':
      return 'L\'importo non è valido. Puoi usare solo cifre, senza punti, virgole o simbolo €. Scrivi per esempio 12000.';
    case 'IMPORTO_OLTRE_LIMITE':
      return 'L\'importo supera il limite. L\'app accetta importi fino a 999999 €. Correggi il campo.';
    default:
      return 'Il valore non è valido. Controlla il campo e correggilo.';
  }
}

// ---------------------------------------------------------------------------
// S-05 Amministrazione
// ---------------------------------------------------------------------------
export const amministrazione = {
  titolo: 'Amministrazione',
  testoFisso: 'I dati di questa demo sono inventati, ispirati all\'Indagine ISTAT sulle spese delle famiglie.',
  titoloDatiAttuali: 'Dati attuali',
  nessunDatoCaricato: 'Nessun dato caricato.',
  etichettaFile: 'File dati',
  pulsanteCarica: 'Carica',
  caricamentoCompletato: 'Caricamento completato.',
  righeLette: (n: number) => `Righe lette: ${n}`,
  righeImportate: (n: number) => `Righe importate: ${n}`,
  righeScartate: (n: number) => `Righe scartate: ${n}`,
  intestazioneRiga: 'Riga',
  intestazioneMotivo: 'Motivo',
  erroreNessunFileScelto: 'Non hai scelto nessun file. Serve un file da caricare. Scegli un file .csv e premi Carica.',
};

/** Messaggi d'errore del caricamento file (S-05), in tre parti. */
export function erroreFile(codice: string): string {
  switch (codice) {
    case 'FILE_MANCANTE':
      return amministrazione.erroreNessunFileScelto;
    case 'FILE_NON_CSV':
      return 'Il file non è stato caricato. Non è un file CSV. Scegli un file che finisce con .csv.';
    case 'FILE_VUOTO':
      return 'Il file non è stato caricato. Il file è vuoto. Scegli un file .csv con i dati del questionario e delle spese.';
    case 'FILE_TROPPO_GRANDE':
      return 'Il file non è stato caricato. Il file supera 2 MB. Scegli un file più piccolo.';
    case 'FILE_CODIFICA_NON_VALIDA':
      return 'Il file non è stato caricato. Il testo del file non è leggibile. Salva il file in formato UTF-8 e riprova.';
    case 'FILE_INTESTAZIONE_NON_VALIDA':
      return 'Il file non è stato caricato. La prima riga del file non è quella prevista. Controlla l\'intestazione del file.';
    case 'FILE_SENZA_DATI_VALIDI':
      return 'Il file non è stato caricato. Dopo i controlli non restano dati validi. Controlla le domande e le fasce nel file.';
    default:
      return erroreGenerico(codice);
  }
}

/** Motivo in parole semplici dei codici di scarto delle righe (sezione 4.H del contratto). */
export function motivoScarto(codice: string): string {
  const mappa: Record<string, string> = {
    NUMERO_COLONNE_ERRATO: 'La riga non ha il numero di colonne previsto.',
    TIPO_RIGA_SCONOSCIUTO: 'Il tipo di riga non è uno di quelli previsti.',
    CAMPO_MANCANTE: 'Manca un campo obbligatorio per questo tipo di riga.',
    CODICE_NON_VALIDO: 'Un codice della riga usa caratteri non ammessi o supera la lunghezza massima.',
    TESTO_TROPPO_LUNGO: 'Un testo della riga supera la lunghezza massima.',
    TIPO_RISPOSTA_SCONOSCIUTO: 'Il tipo di risposta non è scelta né numero.',
    VOCE_SCONOSCIUTA: 'La voce di spesa non è una delle 5 previste.',
    VALORE_NEGATIVO: 'Un valore della riga è sotto zero.',
    NUMERO_NON_VALIDO: 'Un numero della riga non è scritto in modo valido o supera il limite.',
    MINIMO_MAGGIORE_MASSIMO: 'Il minimo della fascia è più grande del massimo.',
    RIF_DOMANDA_INESISTENTE: 'La riga si riferisce a una domanda che non esiste.',
    RIF_FASCIA_INESISTENTE: 'La riga si riferisce a una fascia di punteggio che non esiste.',
    TIPO_RISPOSTA_NON_COERENTE: 'Il tipo di riga non è coerente con il tipo della domanda.',
    CODICE_DUPLICATO: 'Il codice della riga è già stato usato.',
    FASCIA_SOVRAPPOSTA: 'La fascia si sovrappone a una già caricata.',
    FASCIA_INCOMPLETA: 'La fascia di punteggio non ha un importo per tutte le voci.',
    DOMANDA_SENZA_RISPOSTE: 'La domanda non ha nessuna opzione o fascia di risposta valida.',
  };
  return mappa[codice] ?? 'Motivo non riconosciuto.';
}

// ---------------------------------------------------------------------------
// Messaggi generici (per codici di errore senza campo associato, sezione 4.F)
// ---------------------------------------------------------------------------
export const nessunDato = {
  messaggio: 'Non ci sono ancora dati per il confronto. Puoi caricarli dalla pagina Amministrazione.',
  collegamento: 'Vai ad Amministrazione',
};

export function erroreGenerico(codice: string): string {
  const mappa: Record<string, string> = {
    RICHIESTA_NON_VALIDA: 'La richiesta non è valida. Ricarica la pagina e riprova.',
    RISORSA_NON_TROVATA: 'La pagina richiesta non esiste. Torna all\'inizio.',
    METODO_NON_AMMESSO: 'L\'operazione non è permessa. Ricarica la pagina e riprova.',
    ERRORE_INTERNO: 'Si è verificato un problema imprevisto. Riprova più tardi.',
    RETE_NON_DISPONIBILE: 'La richiesta non è arrivata al server. Controlla la connessione e riprova.',
    NESSUN_DATO: nessunDato.messaggio,
  };
  return mappa[codice] ?? mappa['ERRORE_INTERNO'];
}

// ---------------------------------------------------------------------------
// Pagina "Nessun dato"
// ---------------------------------------------------------------------------
export const noData = {
  titolo: 'Nessun dato disponibile',
  messaggio: nessunDato.messaggio,
  collegamento: nessunDato.collegamento,
};
