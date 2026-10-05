---
name: formato-csv-istat
description: Specifica unica del file di dati ISTAT (fittizi, per la demo) caricato dalla pagina di amministrazione - tabelle popolate (domande del profilo, score, dimensioni di spesa per score), colonne, regole di validazione e codici di scarto. Da usare per progettare, implementare, testare o preparare l'import dei dati.
---

# Formato del file dati ISTAT

## Modello concettuale (dall'idea dell'utente)
Il caricamento dalla pagina di amministrazione popola **almeno tre tabelle**:
1. **Domande del profilo**: le domande che definiscono il profilo dell'utente, con il tipo di risposta (scelta tra opzioni, numero, provincia…).
2. **Score**: per ogni domanda, lo score associato alla risposta (o alla fascia di risposta).
3. **Dimensioni di spesa per score**: per ogni score, l'importo atteso per ciascuna dimensione di spesa (es. Casa, Sport e tempo libero, Auto e mobilità, Utenze, Spesa).

I dati sono **fittizi ma plausibili**, ispirati alle statistiche ISTAT sulle spese delle famiglie, e devono essere dichiarati come tali nell'app e nel README.

## Regole che valgono comunque
- Una riga non valida viene **scartata con numero di riga e codice motivo**; l'import delle altre righe **continua**.
- Errori sull'intero file (file vuoto, non CSV, troppo grande, intestazione sbagliata) → import **rifiutato**, nessun dato salvato.
- Limite del file: **2 MB**.
- Codifica UTF-8 (BOM tollerato), separatore `;`, decimali con `,` o `.`, nessun simbolo € nei numeri.
- Un nuovo import valido **sostituisce** i dati precedenti, all'interno di una transazione: o tutto, o niente.
- Coerenza referenziale: uno score che richiama una domanda inesistente, o una dimensione che richiama uno score inesistente, è una riga scartata con codice dedicato.

## Formato di dettaglio

> Fonte unica delle regole del file. Il contratto API (`agents/docs/contratto-api.md`, sezioni 3.3 e 4.H) riporta solo i nomi e il significato dei codici.

### 1. Struttura del file
- **Un solo file CSV**. Il tipo di ogni riga è dato dalla prima colonna `tipo_riga`; tutte le righe hanno le **stesse 13 colonne**.
- Codifica **UTF-8** (un BOM iniziale si ignora). Fine riga `LF` o `CRLF`. Separatore `;`. **Nessuna virgoletta**: il carattere `"` è un carattere normale e i testi non possono contenere `;`.
- Ogni campo si legge **togliendo gli spazi all'inizio e alla fine**.
- **Riga 1 = intestazione**, uguale a questa (maiuscole/minuscole indifferenti, spazi esterni ignorati):
  ```
  tipo_riga;domanda;tipo_risposta;etichetta;testo;spiegazione;opzione;minimo;massimo;punteggio;fascia;voce;importo
  ```
- **Righe vuote** (solo spazi, oppure con tutti i 13 campi vuoti): si ignorano, non sono né lette né scartate.
- **Numero di riga** = riga fisica del file, con l'intestazione = riga 1 (le righe vuote contano nella numerazione).
- **Ordine**: una riga può riferirsi solo a domande o fasce **accettate in righe precedenti**. Le domande compaiono nell'app nell'ordine del file; le opzioni nell'ordine del file.
- **Conteggi**: `righeLette` = righe dopo l'intestazione non vuote; `righeImportate` = righe accettate e non scartate dai controlli finali; `righeScartate` = `righeLette − righeImportate`.

### 2. Colonne
| # | Colonna | Contenuto |
|---|---|---|
| 1 | `tipo_riga` | `DOMANDA`, `OPZIONE`, `FASCIA_RISPOSTA`, `FASCIA_PUNTEGGIO`, `SPESA` (maiuscole/minuscole indifferenti) |
| 2 | `domanda` | codice della domanda (definita da `DOMANDA`, richiamata da `OPZIONE` e `FASCIA_RISPOSTA`) |
| 3 | `tipo_risposta` | `SCELTA` o `NUMERO` (maiuscole/minuscole indifferenti) |
| 4 | `etichetta` | etichetta breve della domanda (usata in "Con chi ti confronti"), max 40 caratteri |
| 5 | `testo` | per `DOMANDA`: testo della domanda, max 200; per `OPZIONE`: testo dell'opzione mostrato all'utente, max 100 |
| 6 | `spiegazione` | breve spiegazione della domanda, facoltativa, max 200 |
| 7 | `opzione` | codice dell'opzione, unico nella sua domanda |
| 8 | `minimo` | estremo inferiore (incluso) di una fascia |
| 9 | `massimo` | estremo superiore (incluso) di una fascia |
| 10 | `punteggio` | punteggio di un'opzione o di una fascia di risposta |
| 11 | `fascia` | codice della fascia di punteggio (definita da `FASCIA_PUNTEGGIO`, richiamata da `SPESA`) |
| 12 | `voce` | `CASA`, `SPORT_TEMPO_LIBERO`, `AUTO_MOBILITA`, `UTENZE`, `SPESA` (maiuscole/minuscole indifferenti) |
| 13 | `importo` | spesa campione annua in euro della voce per la fascia |

### 3. Campi per tipo di riga
`O` = obbligatorio, `F` = facoltativo, `–` = ignorato (qualunque contenuto, anche vuoto).

| tipo_riga | domanda | tipo_risposta | etichetta | testo | spiegazione | opzione | minimo | massimo | punteggio | fascia | voce | importo |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| `DOMANDA` | O | O | O | O | F | – | – | – | – | – | – | – |
| `OPZIONE` (risposta di una domanda `SCELTA`) | O | – | – | O | – | O | – | – | O | – | – | – |
| `FASCIA_RISPOSTA` (risposta di una domanda `NUMERO`) | O | – | – | – | – | – | O | O | O | – | – | – |
| `FASCIA_PUNTEGGIO` | – | – | – | – | – | – | O | O | – | O | – | – |
| `SPESA` | – | – | – | – | – | – | – | – | – | O | O | O |

### 4. Valori ammessi
| Campo | Regola |
|---|---|
| codici (`domanda`, `opzione`, `fascia`) | solo lettere, cifre e `_`, da 1 a 30 caratteri; si convertono in **maiuscolo** (`mi` = `MI`) |
| `etichetta`, `testo`, `spiegazione` | lunghezze massime della sezione 2 |
| `minimo`, `massimo` | interi, solo cifre `0-9`, da 0 a 999999999 |
| `punteggio` | intero, solo cifre `0-9`, da 0 a 1000 |
| `importo` | da 0 a 999999,99: cifre `0-9`, eventualmente seguite da `,` o `.` e 1 o 2 cifre decimali (es. `4200,50`, `4200.5`). Vietati separatori delle migliaia (`12.000` non è valido: dopo il punto ci sono 3 cifre), spazi interni e `€`. Si conserva il valore esatto; l'arrotondamento all'euro (da ,50 in su) si fa nel calcolo (RB-11). |
| numeri negativi | in qualsiasi colonna numerica, `-` seguito da un numero altrimenti valido → `VALORE_NEGATIVO` |
| `voce` | uno dei 5 codici della sezione 2 |

### 5. Controlli sul file intero (caricamento rifiutato, nessun dato cambiato)
Nell'ordine; il primo che fallisce dà il codice d'errore (contratto, sezione 3.3):
1. `FILE_MANCANTE` – nessun file nella richiesta.
2. `FILE_TROPPO_GRANDE` – oltre 2 MB (2 097 152 byte).
3. `FILE_NON_CSV` – il nome non finisce con `.csv` (maiuscole/minuscole indifferenti).
4. `FILE_VUOTO` – 0 byte, oppure solo BOM, spazi e righe vuote.
5. `FILE_CODIFICA_NON_VALIDA` – il contenuto non è UTF-8 valido.
6. `FILE_INTESTAZIONE_NON_VALIDA` – la riga 1 non è l'intestazione della sezione 1.
7. (lettura delle righe, sezioni 6 e 7)
8. `FILE_SENZA_DATI_VALIDI` – dopo gli scarti non restano **almeno 1 domanda** (con almeno 1 opzione o fascia di risposta) **e almeno 1 fascia di punteggio completa** delle 5 voci; vale anche per un file con la sola intestazione (RB-22).

### 6. Controlli su ogni riga (riga scartata, le altre continuano)
I controlli si fanno **in quest'ordine**; il **primo** che fallisce dà il codice di scarto e la riga non viene usata.

| Passo | Codice | Quando |
|---|---|---|
| 1 | `NUMERO_COLONNE_ERRATO` | la riga non ha esattamente 13 campi separati da `;` |
| 2 | `TIPO_RIGA_SCONOSCIUTO` | `tipo_riga` non è uno dei 5 tipi |
| 3 | `CAMPO_MANCANTE` | un campo `O` della sezione 3 è vuoto |
| 4 | `CODICE_NON_VALIDO` / `TESTO_TROPPO_LUNGO` | un codice non rispetta la sezione 4 / un testo supera la sua lunghezza (controllo per colonna, da sinistra a destra) |
| 5 | `TIPO_RISPOSTA_SCONOSCIUTO` | (`DOMANDA`) `tipo_risposta` non è `SCELTA` né `NUMERO` |
| 5 | `VOCE_SCONOSCIUTA` | (`SPESA`) `voce` non è una delle 5 previste |
| 6 | `VALORE_NEGATIVO` / `NUMERO_NON_VALIDO` | un campo numerico usato è negativo / non è scritto come in sezione 4 o supera il massimo (per colonna: `minimo`, `massimo`, `punteggio`, `importo`) |
| 7 | `MINIMO_MAGGIORE_MASSIMO` | (`FASCIA_RISPOSTA`, `FASCIA_PUNTEGGIO`) `minimo` > `massimo` |
| 8 | `RIF_DOMANDA_INESISTENTE` | (`OPZIONE`, `FASCIA_RISPOSTA`) la domanda non è stata accettata in una riga precedente |
| 8 | `RIF_FASCIA_INESISTENTE` | (`SPESA`) la fascia di punteggio non è stata accettata in una riga precedente |
| 9 | `TIPO_RISPOSTA_NON_COERENTE` | `OPZIONE` per una domanda `NUMERO`, oppure `FASCIA_RISPOSTA` per una domanda `SCELTA` |
| 10 | `CODICE_DUPLICATO` | `DOMANDA` con codice già accettato; `OPZIONE` con codice già accettato per la stessa domanda; `FASCIA_PUNTEGGIO` con codice già accettato; `SPESA` con voce già accettata per la stessa fascia |
| 10 | `FASCIA_SOVRAPPOSTA` | `FASCIA_RISPOSTA` che ha almeno un valore in comune con una fascia già accettata della stessa domanda; `FASCIA_PUNTEGGIO` che ha almeno un valore in comune con una fascia di punteggio già accettata (`a.minimo ≤ b.massimo` e `b.minimo ≤ a.massimo`) |

### 7. Controlli finali (dopo aver letto tutte le righe)
| Codice | Quando | Righe scartate |
|---|---|---|
| `FASCIA_INCOMPLETA` | una fascia di punteggio accettata non ha un `SPESA` accettato per **tutte e 5** le voci | la riga `FASCIA_PUNTEGGIO` **e** tutte le sue righe `SPESA` accettate |
| `DOMANDA_SENZA_RISPOSTE` | una domanda accettata non ha nessuna `OPZIONE` o `FASCIA_RISPOSTA` accettata | la riga `DOMANDA` |

Poi si applica il controllo 8 della sezione 5. Le fasce di risposta di una domanda possono lasciare **vuoti** tra loro (RB-07); le fasce di punteggio possono lasciare vuoti tra loro (RB-10).

### 8. File di esempio (in questa cartella)
Dati **inventati**, ispirati all'Indagine ISTAT sulle spese delle famiglie: non sono dati ISTAT reali.

**Domande e punteggi** (uguali nei due file)
| # | Codice | Etichetta | Tipo | Risposte → punteggio | Valori ammessi |
|---|---|---|---|---|---|
| 1 | `PROVINCIA` | Provincia | SCELTA | Milano 3, Roma 3, Torino 2, Bologna 2, Firenze 2, Napoli 1, Palermo 1, Altra provincia 1 | – |
| 2 | `ETA` | Età | NUMERO | 18–35 → 1, 36–50 → 2, 51–65 → 2, 66–99 → 1 | 18–99 |
| 3 | `ABITAZIONE` | Dove vivi | SCELTA | Appartamento di proprietà 1, Affitto 3 | – |
| 4 | `SESSO` | Sesso | SCELTA | M 1, F 1, Preferisco non rispondere 0 (con spiegazione di RB-23) | – |
| 5 | `REDDITO` | Reddito lordo annuo | NUMERO | 0–15000 → 1, 15001–35000 → 2, 35001–60000 → 3, 60001–200000 → 4 (con spiegazione di RB-23) | 0–200000 |
| 6 | `SPORT` | Fai sport | SCELTA | Sì 2, No 1 | – |
| 7 | `AUTOVETTURE` | Numero di autovetture | NUMERO | 0 → 0, 1 → 2, 2–5 → 4 | 0–5 |
| 8 | `CICLOMOTORI` | Numero di ciclomotori | NUMERO | 0 → 0, 1 → 1, 2–5 → 2 | 0–5 |
| 9 | `NUCLEO` | Membri del nucleo familiare | NUMERO | 1 → 1, 2 → 2, 3–4 → 3, 5–10 → 4 | 1–10 |

Punteggio minimo possibile 6, massimo 25.

**Fasce di punteggio e spese campione annue** (in euro, valori del file)
| Fascia | Punteggio | Casa | Sport e tempo libero | Auto e mobilità | Utenze | Spesa |
|---|---|---|---|---|---|---|
| F1 | 6–10 | 7000 | 1500 | 2000 | 3500 | 5500 |
| F2 | 11–13 | 9500 | 2200 | 3500 | 4200,50 (→ 4201 nel calcolo) | 6800 |
| F3 | 14–18 | 12000 | 3000 | 5000 | 5000 | 8000 |
| F4 | 23–25 | 9000 | 2500 | 7500 | 6500 (voce scritta `Utenze`: maiuscole indifferenti) | 11000 |

Tra 19 e 22 non c'è nessuna fascia: serve al profilo fuori fascia.

**Profili attesi (RB-23, CA-09, CA-10)**
| Risposta | Profilo di Marco | punti | Profilo fuori fascia | punti |
|---|---|---|---|---|
| Provincia | Milano | 3 | Milano | 3 |
| Età | 34 | 1 | 70 | 1 |
| Dove vivi | Affitto | 3 | Appartamento di proprietà | 1 |
| Sesso | M | 1 | F | 1 |
| Reddito lordo annuo | 28000 | 2 | 150000 | 4 |
| Fai sport | Sì | 2 | No | 1 |
| Numero di autovetture | 1 | 2 | 3 | 4 |
| Numero di ciclomotori | 0 | 0 | 2 | 2 |
| Membri del nucleo familiare | 1 | 1 | 6 | 4 |
| **Punteggio Z** | | **15** | | **21** |
| **Fascia usata X–Y** | | **F3: 14–18** (esatta) | | **F4: 23–25** (approssimata, RB-10: distanza da F3 = 21 − 18 = 3, da F4 = 23 − 21 = 2) |
| Spese campione | | 12.000 / 3.000 / 5.000 / 5.000 / 8.000 € | | 9.000 / 2.500 / 7.500 / 6.500 / 11.000 € |

Avviso atteso per il profilo fuori fascia: "Nei dati non c'è un gruppo con il tuo punteggio (21). Ti confrontiamo con il gruppo più vicino: punteggio da 23 a 25."
Con "Preferisco non rispondere" al posto di M, Marco ha punteggio 14: resta in F3.

**`esempio_istat.csv`** (file valido, CA-11): 66 righe lette, 66 importate, 0 scartate; "Dati attuali": 9 domande, 4 fasce.

**`esempio_istat_errori.csv`** (CA-12): le righe 1–67 sono identiche al file valido; le righe 68–80 contengono errori di 9 tipi diversi. Esito: **79 lette, 66 importate, 13 scartate**; "Dati attuali": 9 domande, 4 fasce; i profili di Marco e fuori fascia danno gli stessi risultati del file valido.
| Riga | Contenuto (in breve) | Codice di scarto |
|---|---|---|
| 68 | `DOMANDA` `TITOLO_STUDIO` senza testo della domanda | `CAMPO_MANCANTE` |
| 69 | `OPZIONE` di `TITOLO_STUDIO` (domanda non accettata) | `RIF_DOMANDA_INESISTENTE` |
| 70 | `FASCIA_RISPOSTA` `ETA` 30–40 | `FASCIA_SOVRAPPOSTA` |
| 71 | `FASCIA_RISPOSTA` `AUTOVETTURE` con punteggio `due` | `NUMERO_NON_VALIDO` |
| 72 | `SPESA` `F3` voce `VACANZE` | `VOCE_SCONOSCIUTA` |
| 73 | riga di tipo `NOTA` | `TIPO_RIGA_SCONOSCIUTO` |
| 74 | `SPESA;F3;CASA;12000` (4 colonne) | `NUMERO_COLONNE_ERRATO` |
| 75 | `FASCIA_PUNTEGGIO` `F5` 26–30 (manca un importo valido per Casa) | `FASCIA_INCOMPLETA` |
| 76 | `SPESA` `F5` `CASA` importo `-300` | `VALORE_NEGATIVO` |
| 77 | `SPESA` `F5` `SPORT_TEMPO_LIBERO` | `FASCIA_INCOMPLETA` |
| 78 | `SPESA` `F5` `AUTO_MOBILITA` | `FASCIA_INCOMPLETA` |
| 79 | `SPESA` `F5` `UTENZE` | `FASCIA_INCOMPLETA` |
| 80 | `SPESA` `F5` `SPESA` | `FASCIA_INCOMPLETA` |

**File per CA-13** (li prepara il tester, non sono in questa cartella): una copia di `esempio_istat.csv` rinominata `.txt` → `FILE_NON_CSV`; un `.csv` di 0 byte → `FILE_VUOTO`; un `.csv` oltre 2 MB (per esempio l'esempio valido con righe vuote aggiunte) → `FILE_TROPPO_GRANDE`; l'esempio valido con `tipo_riga` cambiato in `tipo` nell'intestazione → `FILE_INTESTAZIONE_NON_VALIDA`.
