# Contratto API – "In tasca mia"

> Versione 1 (Fase 1). Fonte di verità per `dev-backend` e `dev-frontend`. Dopo l'approvazione dell'utente si cambia solo con una nuova approvazione.
> Requisiti di riferimento: `agents/docs/requisiti-funzionali.md` v2. Formato del file dati: `agents/skills/formato-csv-istat/SKILL.md`.

## 1. Convenzioni

| Tema | Regola |
|---|---|
| Base | Tutti i percorsi iniziano con `/api`. Il frontend usa percorsi relativi (`/api/...`) tramite `proxy.conf.json`. |
| Formato | JSON UTF-8, nomi dei campi in camelCase **esattamente** come in questo file. I campi senza valore sono `null` (mai omessi). |
| Accesso | Simulato. Nessun token, nessun cookie. Il nome utente viaggia come parametro di query `nomeUtente` (codificato con `encodeURIComponent`). |
| `nomeUtente` | Il backend lo normalizza sempre: spazi iniziali/finali tolti, minuscole con `Locale.ROOT` (RB-01). Dopo la normalizzazione deve avere da 1 a 50 caratteri, altrimenti `400 RICHIESTA_NON_VALIDA`. |
| Importi in risposta | Numeri interi, euro all'anno. |
| Date | Stringa `yyyy-MM-ddTHH:mm:ss`, ora locale del server (es. `"2026-10-05T10:15:30"`). |
| Ordine | Le liste arrivano già nell'ordine da mostrare: domande e opzioni nell'ordine del file, voci nell'ordine di RB-15. |
| Testi | Il backend **non restituisce mai frasi per l'utente**: solo codici e dati. I testi stanno in `app/frontend/src/app/testi.ts`. Fanno eccezione i testi che arrivano dal file dati (etichette, testi delle domande, spiegazioni, testi delle opzioni, nome del file). |

### Codici delle voci (ordine fisso RB-15)
`CASA`, `SPORT_TEMPO_LIBERO`, `AUTO_MOBILITA`, `UTENZE`, `SPESA`. Per il totale: `TOTALE`.

## 2. Formato d'errore unico

Ogni risposta con codice HTTP ≥ 400 ha questo corpo:

```json
{
  "codice": "VALIDAZIONE",
  "messaggio": "Dati del profilo non validi",
  "dettagli": [
    { "campo": "importi.CASA", "codice": "IMPORTO_NEGATIVO", "minimo": 0, "massimo": 999999 }
  ]
}
```

- `codice`: uno dei codici di errore della sezione 4.F.
- `messaggio`: testo tecnico breve, **mai mostrato** all'utente.
- `dettagli`: lista, vuota (`[]`) se non serve. Ogni elemento ha sempre i 4 campi `campo`, `codice` (sezione 4.G), `minimo`, `massimo` (interi o `null`). Al massimo **un dettaglio per campo**.

## 3. Endpoint (6)

### 3.1 `POST /api/accesso` – Accesso simulato (S-01, RB-01)
Controlla i limiti dei campi. Non salva nulla. La password non viene conservata né scritta nei log.

Request:
```json
{ "nomeUtente": " Marco ", "password": "qualsiasi" }
```
Regole: `nomeUtente` da 1 a 50 caratteri dopo il trim; `password` da 1 a 100 caratteri (nessun trim; stringa vuota o `null` = mancante).

Response `200`:
```json
{ "nomeUtente": "marco" }
```
Il frontend conserva il `nomeUtente` restituito (già normalizzato) e lo usa in tutte le chiamate successive.

Errori:
| HTTP | codice | dettagli |
|---|---|---|
| 400 | `VALIDAZIONE` | `{ "campo": "nomeUtente" \| "password", "codice": "CAMPO_MANCANTE" \| "CAMPO_TROPPO_LUNGO", "minimo": null, "massimo": 50 \| 100 \| null }` (`massimo` valorizzato solo per `CAMPO_TROPPO_LUNGO`). Ordine: prima `nomeUtente`, poi `password`. |
| 400 | `RICHIESTA_NON_VALIDA` | `[]` (corpo non JSON) |

Esempio CA-01 (nome utente vuoto):
```json
{ "codice": "VALIDAZIONE", "messaggio": "Dati di accesso non validi",
  "dettagli": [ { "campo": "nomeUtente", "codice": "CAMPO_MANCANTE", "minimo": null, "massimo": null } ] }
```

### 3.2 `GET /api/dati` – Dati attuali (S-05 "Dati attuali")
Response `200` (dati caricati):
```json
{ "caricato": true, "nomeFile": "esempio_istat.csv", "caricatoIl": "2026-10-05T10:15:30", "numeroDomande": 9, "numeroFasce": 4 }
```
Response `200` (nessun caricamento):
```json
{ "caricato": false, "nomeFile": null, "caricatoIl": null, "numeroDomande": 0, "numeroFasce": 0 }
```
`numeroFasce` = numero di fasce di punteggio importate.

### 3.3 `POST /api/dati` – Caricamento del file dati (S-05, RB-20, RB-21, RB-22)
Request: `multipart/form-data` con una sola parte di nome **`file`**. Le regole del file (codifica, intestazione, colonne, scarti) sono in `agents/skills/formato-csv-istat/SKILL.md`.

Response `200` (caricamento riuscito, anche con righe scartate). Esempio completo per `esempio_istat_errori.csv`:
```json
{
  "righeLette": 79,
  "righeImportate": 66,
  "righeScartate": 13,
  "scarti": [
    { "riga": 68, "codice": "CAMPO_MANCANTE" },
    { "riga": 69, "codice": "RIF_DOMANDA_INESISTENTE" },
    { "riga": 70, "codice": "FASCIA_SOVRAPPOSTA" },
    { "riga": 71, "codice": "NUMERO_NON_VALIDO" },
    { "riga": 72, "codice": "VOCE_SCONOSCIUTA" },
    { "riga": 73, "codice": "TIPO_RIGA_SCONOSCIUTO" },
    { "riga": 74, "codice": "NUMERO_COLONNE_ERRATO" },
    { "riga": 75, "codice": "FASCIA_INCOMPLETA" },
    { "riga": 76, "codice": "VALORE_NEGATIVO" },
    { "riga": 77, "codice": "FASCIA_INCOMPLETA" },
    { "riga": 78, "codice": "FASCIA_INCOMPLETA" },
    { "riga": 79, "codice": "FASCIA_INCOMPLETA" },
    { "riga": 80, "codice": "FASCIA_INCOMPLETA" }
  ],
  "datiAttuali": { "caricato": true, "nomeFile": "esempio_istat_errori.csv", "caricatoIl": "2026-10-05T10:20:02", "numeroDomande": 9, "numeroFasce": 4 }
}
```
- `scarti` ordinati per `riga` crescente; `riga` = numero di riga fisica nel file (intestazione = riga 1). Con 0 scarti: `"scarti": []`.
- `righeLette = righeImportate + righeScartate`.
- `datiAttuali` ha la stessa forma di `GET /api/dati`.

Errori (il caricamento è rifiutato e i dati precedenti restano invariati; `dettagli` sempre `[]`):
| HTTP | codice | Quando |
|---|---|---|
| 400 | `FILE_MANCANTE` | nessuna parte `file`, o file senza nome |
| 413 | `FILE_TROPPO_GRANDE` | oltre 2 MB |
| 400 | `FILE_NON_CSV` | il nome del file non finisce con `.csv` (maiuscole/minuscole indifferenti) |
| 400 | `FILE_VUOTO` | 0 byte, oppure solo BOM / spazi / righe vuote |
| 400 | `FILE_CODIFICA_NON_VALIDA` | il contenuto non è UTF-8 valido |
| 400 | `FILE_INTESTAZIONE_NON_VALIDA` | la prima riga non è l'intestazione prevista |
| 400 | `FILE_SENZA_DATI_VALIDI` | dopo gli scarti manca 1 domanda con risposte o 1 fascia di punteggio completa (RB-22), oppure c'è solo l'intestazione |

L'ordine dei controlli è quello della skill `formato-csv-istat`, sezione 5.

### 3.4 `GET /api/profilo?nomeUtente=marco` – Questionario e profilo salvato (S-04, RB-03, RB-05, RB-24)
Response `200`:
```json
{
  "statoProfilo": "NON_PIU_COMPLETO",
  "domande": [
    { "codice": "PROVINCIA", "tipo": "SCELTA", "etichetta": "Provincia", "testo": "In quale provincia vivi?", "spiegazione": null,
      "opzioni": [ { "codice": "MI", "testo": "Milano" }, { "codice": "RM", "testo": "Roma" } ],
      "minimo": null, "massimo": null },
    { "codice": "ETA", "tipo": "NUMERO", "etichetta": "Età", "testo": "Quanti anni hai?", "spiegazione": null,
      "opzioni": [], "minimo": 18, "massimo": 99 }
  ],
  "voci": [ "CASA", "SPORT_TEMPO_LIBERO", "AUTO_MOBILITA", "UTENZE", "SPESA" ],
  "risposte": { "PROVINCIA": "MI" },
  "importi": { "CASA": 12000, "SPORT_TEMPO_LIBERO": 2400, "AUTO_MOBILITA": 6200, "UTENZE": 5000, "SPESA": 8000 },
  "salvatoIl": "2026-10-05T10:25:00"
}
```
- `statoProfilo`: `COMPLETO` | `NON_PIU_COMPLETO` | `ASSENTE` (sezione 4.C).
- `minimo`/`massimo` (solo per `NUMERO`): il minimo più basso e il massimo più alto delle fasce di risposta (RB-07).
- `risposte`: solo le risposte salvate **ancora valide** con i dati attuali, come mappa `codiceDomanda → valore` (codice opzione per `SCELTA`, numero intero in forma di stringa senza zeri iniziali per `NUMERO`). `{}` se `ASSENTE`.
- `importi`: i 5 importi salvati, oppure `null` se `ASSENTE`. `salvatoIl`: `null` se `ASSENTE`.

Errori:
| HTTP | codice | Quando |
|---|---|---|
| 409 | `NESSUN_DATO` | nessun file dati mai caricato (RB-25) |
| 400 | `RICHIESTA_NON_VALIDA` | `nomeUtente` mancante o non valido |

### 3.5 `PUT /api/profilo?nomeUtente=marco` – Salvataggio del profilo (S-04, RB-05, RB-06, RB-07)
Il frontend invia **i valori così come scritti** (stringhe), senza controllarli: tutte le regole stanno nel backend. O si salva tutto, o niente.

Request:
```json
{
  "risposte": { "PROVINCIA": "MI", "ETA": "34", "ABITAZIONE": "AFFITTO", "SESSO": "M", "REDDITO": "28000",
                "SPORT": "SI", "AUTOVETTURE": "1", "CICLOMOTORI": "0", "NUCLEO": "1" },
  "importi": { "CASA": "12000", "SPORT_TEMPO_LIBERO": "2400", "AUTO_MOBILITA": "6200", "UTENZE": "5000", "SPESA": "8000" }
}
```
- Valori stringa o `null`. Chiavi di `risposte` sconosciute: ignorate. Una domanda o voce senza chiave = mancante.
- Il nome utente nuovo crea il profilo; uno esistente lo sostituisce (risposte, importi, `salvatoIl`).

Response `200`:
```json
{ "statoProfilo": "COMPLETO", "salvatoIl": "2026-10-05T10:25:00" }
```

Errori:
| HTTP | codice | dettagli |
|---|---|---|
| 400 | `VALIDAZIONE` | un dettaglio per ogni campo non valido; `campo` = `risposte.<CODICE_DOMANDA>` o `importi.<CODICE_VOCE>`; ordine: domande nell'ordine del file, poi voci nell'ordine RB-15 |
| 409 | `NESSUN_DATO` | nessun file dati caricato |
| 400 | `RICHIESTA_NON_VALIDA` | `nomeUtente` non valido o corpo non JSON |

Regole di validazione (prima regola che fallisce per ogni campo):
| Campo | Condizione (dopo il trim) | codice dettaglio | minimo / massimo |
|---|---|---|---|
| risposta `SCELTA` | vuota, `null` o codice opzione non esistente | `RISPOSTA_MANCANTE` | null / null |
| risposta `NUMERO` | vuota o `null` | `RISPOSTA_MANCANTE` | null / null |
| risposta `NUMERO` | non solo cifre `0-9`, oppure fuori da minimo–massimo della domanda | `NUMERO_NON_VALIDO` | minimo / massimo della domanda |
| importo | vuoto o `null` | `IMPORTO_MANCANTE` | null / null |
| importo | `-` seguito solo da cifre (es. `-100`) | `IMPORTO_NEGATIVO` | 0 / 999999 |
| importo | contiene caratteri diversi da `0-9` (es. `12.000`, `12,5`, `300 €`, `abc`) | `IMPORTO_NON_CIFRE` | 0 / 999999 |
| importo | solo cifre, valore > 999999 (zeri iniziali ignorati; nessun overflow con stringhe lunghe) | `IMPORTO_OLTRE_LIMITE` | 0 / 999999 |

Esempio CA-04:
```json
{ "codice": "VALIDAZIONE", "messaggio": "Dati del profilo non validi", "dettagli": [
  { "campo": "importi.CASA",   "codice": "IMPORTO_NEGATIVO",  "minimo": 0,    "massimo": 999999 },
  { "campo": "importi.UTENZE", "codice": "IMPORTO_NON_CIFRE", "minimo": 0,    "massimo": 999999 },
  { "campo": "importi.SPESA",  "codice": "IMPORTO_MANCANTE",  "minimo": null, "massimo": null } ] }
```

### 3.6 `GET /api/analisi?nomeUtente=marco` – Confronto (S-03, RB-08 … RB-13, RB-15, RB-18)
Calcolato a ogni chiamata con i dati caricati in quel momento. Il frontend non fa calcoli: mostra questi valori.

Response `200` (profilo di Marco con gli importi dell'esempio):
```json
{
  "punteggio": 15,
  "fascia": { "codice": "F3", "minimo": 14, "massimo": 18 },
  "approssimato": false,
  "risposte": [
    { "etichetta": "Provincia", "tipo": "SCELTA", "testoOpzione": "Milano", "numero": null },
    { "etichetta": "Età", "tipo": "NUMERO", "testoOpzione": null, "numero": 34 },
    { "etichetta": "Reddito lordo annuo", "tipo": "NUMERO", "testoOpzione": null, "numero": 28000 }
  ],
  "voci": [
    { "voce": "CASA", "spesaCorrente": 12000, "spesaCampione": 12000, "differenza": 0, "differenzaAssoluta": 0, "percentuale": 0, "esito": "IN_LINEA", "fraseCifra": "IN_LINEA_UGUALE" },
    { "voce": "SPORT_TEMPO_LIBERO", "spesaCorrente": 2400, "spesaCampione": 3000, "differenza": -600, "differenzaAssoluta": 600, "percentuale": 20, "esito": "SOTTO", "fraseCifra": "SOTTO_PERCENTUALE" },
    { "voce": "AUTO_MOBILITA", "spesaCorrente": 6200, "spesaCampione": 5000, "differenza": 1200, "differenzaAssoluta": 1200, "percentuale": 24, "esito": "SOPRA", "fraseCifra": "SOPRA_PERCENTUALE" },
    { "voce": "UTENZE", "spesaCorrente": 5000, "spesaCampione": 5000, "differenza": 0, "differenzaAssoluta": 0, "percentuale": 0, "esito": "IN_LINEA", "fraseCifra": "IN_LINEA_UGUALE" },
    { "voce": "SPESA", "spesaCorrente": 8000, "spesaCampione": 8000, "differenza": 0, "differenzaAssoluta": 0, "percentuale": 0, "esito": "IN_LINEA", "fraseCifra": "IN_LINEA_UGUALE" }
  ],
  "totale": { "voce": "TOTALE", "spesaCorrente": 33600, "spesaCampione": 33000, "differenza": 600, "differenzaAssoluta": 600, "percentuale": 2, "esito": "IN_LINEA", "fraseCifra": "IN_LINEA_PIU" },
  "scalaMassima": 12000
}
```
(nell'esempio `risposte` è abbreviato: in realtà contiene una riga per **ogni** domanda, nell'ordine delle domande.)

Significato dei campi:
| Campo | Regola |
|---|---|
| `punteggio` | Z, somma dei punteggi delle risposte (RB-08, RB-09) |
| `fascia` | fascia usata, X = `minimo`, Y = `massimo` (RB-09 o RB-10) |
| `approssimato` | `true` se il punteggio non cade in nessuna fascia ed è stata usata la più vicina (RB-10): il frontend mostra l'avviso |
| `risposte` | per RB-18: `testoOpzione` valorizzato per `SCELTA`, `numero` per `NUMERO` (il frontend lo formatta con il punto delle migliaia) |
| `spesaCampione` | importo della fascia arrotondato all'euro, da ,50 in su per eccesso (RB-11); per `TOTALE` somma dei 5 arrotondati |
| `differenza` | `spesaCorrente − spesaCampione`, con segno (RB-12) |
| `differenzaAssoluta` | D = \|differenza\| (usato nelle frasi di tabella B) |
| `percentuale` | P = \|differenza\| ÷ spesaCampione × 100, arrotondata all'intero da ,5 in su; `null` se `spesaCampione = 0` (RB-12) |
| `esito` | `SOPRA` \| `SOTTO` \| `IN_LINEA` (RB-13) |
| `fraseCifra` | quale riga della tabella B di RB-17 usare (sezione 4.B) |
| `scalaMassima` | valore più alto tra i 10 importi delle 5 voci (corrente e campione), per la scala comune della ragnatela (RB-15); può essere 0 |

Errori:
| HTTP | codice | Azione del frontend |
|---|---|---|
| 409 | `NESSUN_DATO` | mostra il messaggio di RB-25 in S-03 |
| 409 | `PROFILO_ASSENTE` | va a S-04 con l'avviso "Per vedere l'analisi di spesa compila prima il tuo profilo." |
| 409 | `PROFILO_NON_PIU_COMPLETO` | va a S-04 (che mostra l'avviso "Le domande del profilo sono cambiate…") |
| 400 | `RICHIESTA_NON_VALIDA` | messaggio generico |

## 4. Elenco chiuso dei codici

Il frontend ricava da questi codici **tutti** i testi in `testi.ts`. Un codice non presente qui non può comparire.

### 4.A Esiti del confronto (`esito`)
| Codice | Significato |
|---|---|
| `SOPRA` | spesa corrente più alta della campione di oltre il 10%; con campione 0 €, qualsiasi spesa > 0 € |
| `IN_LINEA` | \|differenza\| ≤ 10% della spesa campione; sempre se differenza = 0 € |
| `SOTTO` | spesa corrente più bassa della campione di oltre il 10% |

### 4.B Frase con la cifra (`fraseCifra`, tabella B di RB-17)
| Codice | Riga della tabella B | Usa |
|---|---|---|
| `SOPRA_PERCENTUALE` | SOPRA, P > 10 | D, P |
| `SOPRA_OLTRE_10` | SOPRA, P = 10 | D |
| `SOTTO_PERCENTUALE` | SOTTO, P > 10 | D, P |
| `SOTTO_OLTRE_10` | SOTTO, P = 10 | D |
| `IN_LINEA_PIU` | IN_LINEA, D > 0, differenza positiva ("in più") | D |
| `IN_LINEA_MENO` | IN_LINEA, D > 0, differenza negativa ("in meno") | D |
| `IN_LINEA_UGUALE` | IN_LINEA, D = 0 | – |
| `SOPRA_CAMPIONE_ZERO` | SOPRA con campione 0 € | D |

### 4.C Stato del profilo (`statoProfilo`)
| Codice | Significato |
|---|---|
| `COMPLETO` | profilo salvato con risposta valida a ogni domanda attuale e 5 importi (RB-05) |
| `NON_PIU_COMPLETO` | profilo salvato, ma almeno una domanda attuale non ha più una risposta valida (RB-24) |
| `ASSENTE` | nessun profilo salvato per questo nome utente |

### 4.D Tipo di domanda (`tipo`)
`SCELTA` (scelta singola tra `opzioni`), `NUMERO` (numero intero tra `minimo` e `massimo`).

### 4.E Voci
`CASA`, `SPORT_TEMPO_LIBERO`, `AUTO_MOBILITA`, `UTENZE`, `SPESA`, `TOTALE` (vedi sezione 1).

### 4.F Codici di errore (`codice` del formato d'errore)
| Codice | HTTP | Endpoint |
|---|---|---|
| `VALIDAZIONE` | 400 | `POST /api/accesso`, `PUT /api/profilo` |
| `RICHIESTA_NON_VALIDA` | 400 | tutti (parametro mancante o non valido, corpo non leggibile) |
| `FILE_MANCANTE` | 400 | `POST /api/dati` |
| `FILE_NON_CSV` | 400 | `POST /api/dati` |
| `FILE_VUOTO` | 400 | `POST /api/dati` |
| `FILE_TROPPO_GRANDE` | 413 | `POST /api/dati` |
| `FILE_CODIFICA_NON_VALIDA` | 400 | `POST /api/dati` |
| `FILE_INTESTAZIONE_NON_VALIDA` | 400 | `POST /api/dati` |
| `FILE_SENZA_DATI_VALIDI` | 400 | `POST /api/dati` |
| `NESSUN_DATO` | 409 | `GET/PUT /api/profilo`, `GET /api/analisi` |
| `PROFILO_ASSENTE` | 409 | `GET /api/analisi` |
| `PROFILO_NON_PIU_COMPLETO` | 409 | `GET /api/analisi` |
| `RISORSA_NON_TROVATA` | 404 | percorso inesistente |
| `METODO_NON_AMMESSO` | 405 | metodo HTTP non previsto |
| `ERRORE_INTERNO` | 500 | errore imprevisto |
| `RETE_NON_DISPONIBILE` | – | **solo frontend**: nessuna risposta (stato 0) o timeout di 10 s |

### 4.G Codici di dettaglio di validazione (`dettagli[].codice`)
| Codice | Campo | Testo (requisiti) |
|---|---|---|
| `CAMPO_MANCANTE` | `nomeUtente`, `password` | S-01 "Campo vuoto" |
| `CAMPO_TROPPO_LUNGO` | `nomeUtente` (max 50), `password` (max 100) | S-01 "Campo troppo lungo", con `massimo` |
| `RISPOSTA_MANCANTE` | `risposte.*` | S-04 "Risposta mancante" |
| `NUMERO_NON_VALIDO` | `risposte.*` | S-04 "Numero fuori intervallo o non intero", con `minimo` = X e `massimo` = Y |
| `IMPORTO_MANCANTE` | `importi.*` | S-04 "Risposta mancante" (stesso testo) |
| `IMPORTO_NEGATIVO` | `importi.*` | S-04 "Importo negativo" |
| `IMPORTO_NON_CIFRE` | `importi.*` | S-04 "Importo con caratteri diversi dalle cifre" |
| `IMPORTO_OLTRE_LIMITE` | `importi.*` | S-04 "Importo oltre 999999" |

### 4.H Motivi di scarto delle righe del file (`scarti[].codice`)
Quando si applicano: `agents/skills/formato-csv-istat/SKILL.md` (fonte unica delle regole). Qui il significato, per scrivere il motivo in parole semplici.
| Codice | Significato per il testo |
|---|---|
| `NUMERO_COLONNE_ERRATO` | la riga non ha 13 colonne separate da `;` |
| `TIPO_RIGA_SCONOSCIUTO` | il tipo di riga non è DOMANDA, OPZIONE, FASCIA_RISPOSTA, FASCIA_PUNTEGGIO o SPESA |
| `CAMPO_MANCANTE` | manca un campo obbligatorio per quel tipo di riga |
| `CODICE_NON_VALIDO` | un codice usa caratteri diversi da lettere, cifre e `_`, o supera 30 caratteri |
| `TESTO_TROPPO_LUNGO` | un testo supera la lunghezza massima |
| `TIPO_RISPOSTA_SCONOSCIUTO` | il tipo di risposta non è SCELTA o NUMERO |
| `VOCE_SCONOSCIUTA` | la voce di spesa non è una delle 5 previste |
| `VALORE_NEGATIVO` | un importo o un punteggio è sotto zero |
| `NUMERO_NON_VALIDO` | un numero non è scritto in modo valido o supera il limite |
| `MINIMO_MAGGIORE_MASSIMO` | il minimo di una fascia è più grande del massimo |
| `RIF_DOMANDA_INESISTENTE` | la riga si riferisce a una domanda che non c'è (o non è stata importata) prima di questa riga |
| `RIF_FASCIA_INESISTENTE` | la riga si riferisce a una fascia di punteggio che non c'è prima di questa riga |
| `TIPO_RISPOSTA_NON_COERENTE` | un'opzione per una domanda a numero, o una fascia di risposta per una domanda a scelta |
| `CODICE_DUPLICATO` | il codice (domanda, opzione, fascia) o la voce per la fascia è già stato letto |
| `FASCIA_SOVRAPPOSTA` | la fascia si sovrappone a una già letta |
| `FASCIA_INCOMPLETA` | la fascia di punteggio non ha un importo valido per tutte e 5 le voci |
| `DOMANDA_SENZA_RISPOSTE` | la domanda non ha nessuna opzione o fascia di risposta valida |
