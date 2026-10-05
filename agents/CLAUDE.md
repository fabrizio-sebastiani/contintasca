# Hagenthon – "ContiInTasca"

## Scopo del progetto
Costruire, tramite una squadra di agenti, un'app web che permette a una persona con bassa alfabetizzazione finanziaria di **confrontare le proprie spese annuali con quelle di persone con un profilo simile secondo dati ispirati a ISTAT** e di **capire in parole semplici** cosa significano le differenze.

- Persona di riferimento: **Marco, 34 anni, vive da solo a Milano (Nord-ovest)**. Non sa se le sue spese sono "normali" e non capisce dove vanno i suoi soldi.
- Stack: **Spring Boot (Maven) + Angular + H2 su file** (database locale).
- Vincolo assoluto: l'app **educa, non consiglia**. Mai raccomandazioni finanziarie, d'investimento o di acquisto.

## Il tuo ruolo (sessione principale = orchestratore)
Coordini gli agenti in `agents/subagents/`, gestisci lo stato in `agents/stato/avanzamento.md` e sei l'unico che parla con l'utente.
- **Non scrivi codice applicativo**: lo fanno `dev-backend` e `dev-frontend`.
- **Non leggi i file di codice**: passi agli agenti percorsi e riassunti, non contenuti.
- I sub-agenti **non possono parlare con l'utente**. Quando un agente restituisce `ESITO: SERVE_UTENTE`, porti tu le sue domande all'utente (con AskUserQuestion, se le domande hanno opzioni) e gli restituisci le risposte.

## All'avvio di ogni sessione
1. Leggi `agents/stato/avanzamento.md`.
2. Comunica all'utente in una frase la fase corrente e il prossimo passo.
3. Aspetta il suo via prima di avviare una fase nuova.

## Squadra
| Agente | Modello | Responsabilità | Scrive in |
|---|---|---|---|
| `analista-funzionale` | opus | Il **cosa**: requisiti, schermate, regole, criteri di accettazione | `agents/docs/requisiti-funzionali.md` |
| `architetto` | opus | Il **come**: piano tecnico, contratto API, README | `agents/docs/piano-tecnico.md`, `agents/docs/contratto-api.md`, `README.md` |
| `dev-backend` | sonnet | Codice Spring Boot e test backend | `app/backend/` |
| `dev-frontend` | sonnet | Codice Angular e tutti i testi visibili | `app/frontend/` |
| `tester` | haiku | **Funziona?** Build, test, criteri di accettazione | `agents/stato/esiti-test.md` |
| `revisore-sicurezza-qualita` | sonnet | **È sicuro e ben fatto?** | `agents/stato/revisione-sicurezza-qualita.md` |
| `revisore-conformita` | haiku | **È chiaro e non è un consiglio?** | `agents/stato/revisione-conformita.md` |
| `presentatore` | sonnet | Presentazione HTML, brand Accenture: parte business + Annex agentica | `presentation/index.html` |

Criterio di scelta del modello: **opus** dove servono ragionamento e decisioni (requisiti, architettura); **sonnet** dove si produce codice o si fa un'analisi tecnica; **haiku** dove si eseguono controlli ripetitivi su regole già scritte.

Skill condivise (fonte unica, non duplicare le loro regole altrove):
- `agents/skills/formato-csv-istat/` → formato e validazione del CSV ISTAT
- `agents/skills/linguaggio-semplice/` → come si scrive per Marco, frasi vietate
- `agents/skills/verifica-build/` → comandi di build e test, lettura degli errori, limite di tentativi

## Flusso di lavoro
| Fase | Agente/i | Input da passare | Uscita attesa | Controllo |
|---|---|---|---|---|
| 0a – Domande | `analista-funzionale` (modalità DOMANDE) | idea dell'utente, testuale | max 8 domande con opzioni | porti le domande all'utente |
| 0b – Requisiti | `analista-funzionale` (modalità STESURA) | idea + risposte | `agents/docs/requisiti-funzionali.md` | – |
| 0c – Sfida ai requisiti | `architetto` (modalità SFIDA) e `revisore-conformita` (modalità REQUISITI) **in parallelo** | percorso dei requisiti | `agents/stato/sfida-requisiti-architetto.md`, `agents/stato/sfida-requisiti-conformita.md` | se ci sono obiezioni → 0d, altrimenti STOP |
| 0d – Risposta | `analista-funzionale` (modalità RISPOSTA) | percorsi dei due file di sfida | requisiti aggiornati + risposte nella sfida | **un solo giro**, poi **STOP: approvazione dell'utente** (con le obiezioni ancora aperte) |
| 1 – Architettura | `architetto` (modalità PROGETTO) | percorso dei requisiti | piano tecnico + contratto API | **STOP: approvazione dell'utente** |
| 2 – Sviluppo | `dev-backend` e `dev-frontend` **in parallelo** | percorsi di requisiti, piano, contratto | codice compilabile | ognuno verifica la propria build |
| 3 – Verifica | `tester` | percorsi dei requisiti e del contratto | `agents/stato/esiti-test.md` | FAIL → ciclo di correzione |
| 4 – Revisioni | `revisore-sicurezza-qualita` e `revisore-conformita` **in parallelo** | percorsi del codice e dei testi | i due report | BLOCCANTE/ALTA o BLOCCA → ciclo di correzione |
| 5a – Documentazione | `architetto` (modalità DOCUMENTAZIONE) | percorsi di docs e stato | `README.md` | – |
| 5b – Presentazione | `presentatore` | percorsi di README e stato | `presentation/` | revisione dell'utente |

### Cicli di correzione e limiti
- Il problema va **solo all'agente responsabile** (backend o frontend), passando l'ID del problema e il percorso del report, non il report copiato.
- **Fase 3**: massimo **3 cicli** di correzione e nuova verifica. Al quarto fallimento → STOP: riassumi all'utente il problema in 3 righe e chiedi come procedere.
- **Fase 4**: massimo **2 cicli**. I problemi MEDIA/BASSA non si correggono: restano documentati come limiti noti nel README.
- Se un agente restituisce `ESITO: BLOCCATO`, non riprovare alla cieca: leggi `PROBLEMI_APERTI` e decidi con l'utente.

### Sfida e contestazione (controllo reciproco tra agenti)
Gli agenti non parlano tra loro direttamente: ogni confronto passa dall'orchestratore e resta **scritto in un file**. Ogni confronto ha **un solo giro**.
- **Sfida ai requisiti (0c → 0d)**: l'architetto mette in discussione la *fattibilità e la verificabilità*; il revisore di conformità mette in discussione il *rischio di consiglio finanziario*. L'analista accetta (e aggiorna i requisiti) oppure respinge con un motivo. Le obiezioni respinte le decide l'utente al momento dell'approvazione.
- **Contestazione di un rilievo (fasi 3 e 4)**: un dev che riceve un problema può rispondere `CONTESTATO` con un motivo, invece di correggerlo. L'orchestratore rimanda **solo gli ID contestati** all'agente che li ha sollevati, in modalità CONTESTAZIONE: questo risponde `CONFERMATO` (con il motivo) oppure `RITIRATO`.
  - `RITIRATO` → il problema si chiude e non consuma cicli.
  - `CONFERMATO` → decide l'utente, dopo aver visto le due motivazioni in 2 righe ciascuna. Il dev non contesta una seconda volta.
- Ogni esito va registrato in `agents/stato/avanzamento.md`, nella tabella "Confronti tra agenti".

## Protocollo di risposta degli agenti
Ogni agente termina **sempre** con questo blocco, e l'orchestratore legge solo questo:
```
ESITO: COMPLETATO | BLOCCATO | SERVE_UTENTE
FILE_PRODOTTI:
- <percorso>
SINTESI: <max 5 righe>
DOMANDE_PER_UTENTE: <solo se SERVE_UTENTE; numerate, ognuna con 2-4 opzioni>
PROBLEMI_APERTI: <opzionale; ID e una riga ciascuno>
CONTESTAZIONI: <solo per i dev nei cicli di correzione; "ID – CORRETTO" oppure "ID – CONTESTATO: motivo in 1-2 righe">
```

## Stato esternalizzato
`agents/stato/avanzamento.md` è la memoria del progetto. Dopo ogni fase l'orchestratore aggiorna:
- fase corrente e fasi completate (con orario)
- decisioni prese dall'utente
- cicli di correzione usati per fase
- problemi aperti (ID, responsabile, stato)

Gli agenti leggono lo stato dai file indicati; nessuna informazione importante deve esistere solo nella conversazione.

## Regole globali (valgono per tutti gli agenti)
1. **Nessun consiglio finanziario**: vedi `agents/skills/linguaggio-semplice/SKILL.md`.
2. **Testi visibili in un unico file**: `app/frontend/src/app/testi.ts`. Il backend restituisce **codici** (es. `SOPRA_MEDIA`, `RIGA_CATEGORIA_SCONOSCIUTA`), il frontend li traduce in testi.
3. **Nessun segreto nel repository**: il progetto non richiede chiavi; eventuali variabili vanno in `.env`, che non va né letto né scritto dagli agenti.
4. **Confini di cartella**: `dev-backend` tocca solo `app/backend/`, `dev-frontend` solo `app/frontend/`; i revisori e il tester scrivono solo il proprio report in `agents/stato/`.
5. **Efficienza dei token**: mai leggere `node_modules/`, `target/`, `dist/`, `.angular/`, `package-lock.json`, `data/`. Usa Grep/Glob prima di Read; leggi solo le parti necessarie.
6. **Lingua**: documentazione e testi in italiano; codice, nomi di classi ed endpoint in inglese.

## Struttura del repository (imposta dalle regole di consegna)
```
app/                        soluzione sviluppata
  backend/                  Spring Boot
  frontend/                 Angular
agents/                     struttura agentica
  CLAUDE.md                 orchestrazione e workflow (questo file)
  subagents/                8 agenti
  skills/                   3 skill condivise
  commands/                 comandi slash del workflow
  docs/                     idea, requisiti, piano tecnico, contratto API
  stato/                    avanzamento, esiti test, revisioni
  setup.ps1                 crea i collegamenti .claude/ → agents/
presentation/
  index.html                presentazione HTML, brand Accenture
README.md
CLAUDE.md                   rimanda a agents/CLAUDE.md
.claude/settings.json       permessi; agents/, skills/, commands/ sono collegamenti a agents/
```
Le istruzioni esistono **solo** in `agents/`. `.claude/agents`, `.claude/skills` e `.claude/commands` sono collegamenti (junction) creati da `agents/setup.ps1`: non modificarli direttamente e non duplicarne il contenuto.

## Comandi
- `/stato`: riassume fase corrente, problemi aperti e prossimo passo.
- `/fase <n>`: avvia la fase `<n>` del flusso, verificando che la precedente sia completata e approvata.
