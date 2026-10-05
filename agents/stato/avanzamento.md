# Avanzamento progetto

## Fase corrente
Fase 5a – README (architetto, modalità DOCUMENTAZIONE); presentazione già completa

## Fasi completate
| Fase | Completata alle | Esito |
|---|---|---|
| Setup squadra di agenti, skill, impostazioni | 2026-10-05 | COMPLETATO |
| 0a – Domande dell'analista (8 domande, tutte risposte con l'opzione consigliata) | 2026-10-05 | COMPLETATO |
| 0b – Requisiti (10 US, 5 schermate, 25 RB, 14 CA) | 2026-10-05 | COMPLETATO |
| 0c – Sfida: 6 obiezioni architetto + 1 conformità | 2026-10-05 | COMPLETATO |
| 0d – Risposta analista: 7 accettate su 7, requisiti v2 | 2026-10-05 | COMPLETATO |
| Approvazione requisiti v2 da parte dell'utente | 2026-10-05 | APPROVATO |
| 1 – Architettura: 6 endpoint, 9 task backend, 10 task frontend, formato CSV a 13 colonne, 2 file di esempio | 2026-10-05 | COMPLETATO |
| Approvazione piano tecnico e contratto API (incl. AR-02) | 2026-10-05 | APPROVATO |
| 2 – Sviluppo backend: 9 task, 35 test verdi al primo tentativo | 2026-10-05 | COMPLETATO |
| 2 – Sviluppo frontend: 10 task, ng build verde al secondo tentativo | 2026-10-05 | COMPLETATO |
| 4 (anticipata) – Conformità testi ciclo 1: 1 TESTO_FUORI_FILE (bloccante, RC-03) + 2 MODIFICA (RC-01, RC-02), tutti CORRETTI | 2026-10-05 | COMPLETATO |
| 4 – Sicurezza e qualità ciclo 1: 0 BLOCCANTE, 0 ALTA, 1 MEDIA (SQ-01, limite noto), 0 BASSA | 2026-10-05 | COMPLETATO |
| 3 – Verifica ciclo 2: 13 CA PASS, 0 FAIL, 1 DA_VERIFICARE_IN_DEMO (CA-14); 35/35 test; build frontend OK | 2026-10-05 | COMPLETATO |
| 5b – Presentazione: business (6) + Annex (5), layout Accenture, senza illustrazioni (scelta dell'utente), immagine utente in slide 2 | 2026-10-05 | COMPLETATO |

## Decisioni dell'utente
- Tema 02 – Inclusione finanziaria: confronto delle proprie spese con famiglie simili su dati ISTAT.
- Gli agenti **costruiscono** l'app (non vivono dentro l'app).
- Stack: Spring Boot + Angular + H2 su file.
- I dati ISTAT si caricano manualmente da un caricatore CSV nell'app.
- Presentazione da preparare entro le 2 ore, **in HTML con brand Accenture** (`presentation/index.html`).
- Nome dell'app: **"In tasca mia"**. Login e MFA simulati (vedi `agents/docs/idea-utente.md`).
- Nome dell'app cambiato dall'utente in **"ContiInTasca"** (allineati testi dell'app, documenti, presentazione e script).
- Consegna: repository GitHub pubblico con `app/`, `agents/`, `presentation/` e `README.md`. Il push lo fa l'utente alla fine.
- Sezione "Consigli" rinominata in **"Da sapere"**: contenuti educativi statici, nessuna indicazione su cosa fare (vincolo del tema).
- Le spese di Marco si inseriscono **in fondo al questionario Profilo**, una per dimensione, annuali.
- **Requisiti v2 approvati.** Sesso: M / F / "Preferisco non rispondere" (score 0) con spiegazione. Voce "Esci" nel menu: inclusa. Fonte con anno: "…delle famiglie, anno 2024." (l'utente ha scelto diversamente dal consiglio dell'analista).
- **Presentazione**: pubblico business non tecnico; parte principale business (6 slide) + sezione "Annex" con la struttura agentica. Connessioni dimostrative: ARERA, IVASS + Facile.it/Segugio.it (al posto di Subito.it, che è un sito di annunci), Open Banking, Banca d'Italia, App IO/pagoPA, MIMIT, SosTariffe.
- **Fonti istituzionali**: aggiunta RB-26 "Dove verificare" (rimando neutro a MIMIT, IVASS, ARERA, Banca d'Italia per le voci sopra la media); scartata l'opzione "azioni correttive" esplicite.
- **Presentazione affidata a un collega dell'utente**: da ora `presentation/` non va più modificata né dal `presentatore` né dall'orchestratore. Fase 5b chiusa lato agenti.
- **Pagina iniziale**: dopo l'accesso si va sempre a **Profilo** (modifica dell'utente; cambiato `confirm-page.component.ts`, ng build verde).

## Cicli di correzione usati
| Fase | Cicli usati | Limite |
|---|---|---|
| 3 – Verifica | 1 (F-01: avvio backend bloccato da socket AF_UNIX di Java, non da OneDrive come ipotizzato dal tester; risolto dall'orchestratore con -Djdk.net.unixdomain.tmpdir) | 3 |
| 4 – Revisioni | 1 (conformità testi: RC-01, RC-02, RC-03 → dev-frontend) | 2 |

## Confronti tra agenti
| ID | Sollevato da | Contro | Esito (ACCETTATA / RESPINTA / RITIRATO / CONFERMATO) | Decisione utente |
|---|---|---|---|---|
| OB-A-01 | architetto | analista | ACCETTATA – profilo completo di Marco e profilo fuori fascia | – |
| OB-A-02 | architetto | analista | ACCETTATA – ragnatela con un solo stato "voce mostrata" | – |
| OB-A-03 | architetto | analista | ACCETTATA – frasi esatte "oltre il 10%" | – |
| OB-A-04 | architetto | analista | ACCETTATA – avviso unico in base allo stato del profilo | – |
| OB-A-05 | architetto | analista | ACCETTATA – accesso valido per la scheda del browser | – |
| OB-A-06 | architetto | analista | ACCETTATA – etichetta breve e testo per ogni domanda | – |
| OB-C-01 | revisore-conformita | analista | ACCETTATA con modifica – Sesso con terza opzione e spiegazione | Confermata (M/F/Preferisco non rispondere) |
| F-01 | tester | orchestratore | Diagnosi del tester (OneDrive) CORRETTA dall'orchestratore: causa reale socket AF_UNIX di Java; risolto con -Djdk.net.unixdomain.tmpdir | – |
| DOC-01 | architetto | stato | Incoerenza nel conteggio della revisione di conformità tra stato e report: allineato lo stato al report | – |

## Problemi aperti
| ID | Responsabile | Descrizione | Stato |
|---|---|---|---|

## Backend
_(aggiornato da dev-backend)_

Stato: **COMPLETATO** (task B-01 … B-09, in ordine). Build verde: `mvn -q test` → 35 test eseguiti, 0 falliti, 0 errori (0 cicli di correzione usati).

Progetto creato in `app/backend/` (Spring Boot 4.1.1, Java 21, Maven; dipendenze web/data-jpa/h2/validation da start.spring.io). `application.properties` con H2 su file, console disattivata, limite upload 2 MB, `server.tomcat.max-swallow-size=10MB` (AR-03), nessuno stacktrace/messaggio nelle risposte d'errore. Properties di test con H2 in memoria (`data/` in `.gitignore`).

Package implementati:
- `common`: `ApiError`, `ErrorDetail`, `ApiException`, `GlobalExceptionHandler` (unico, copre tutti i casi della sezione 5 del piano), `UserNames`, `Timestamps`.
- `config.WebConfig`: CORS su `/api/**` da `http://localhost:4200`, GET/POST/PUT.
- `data`: entità (`Question`, `AnswerOption`, `AnswerRange`, `ScoreBand`, `SampleAmount`, `DataLoad`) con vincoli `jakarta.validation`; repository Spring Data; `CsvParser` (regole e ordine dei controlli alla lettera della skill `formato-csv-istat`, incl. `DOMANDA_SENZA_RISPOSTE` e punteggi 0–1000 di AR-02); `DataImportService` (sostituzione transazionale RB-22, log INFO di import completato/rifiutato senza dati personali); `DataController`.
- `profile`: `Profile` (risposte come `@ElementCollection` eager), `ProfileRepository`, `ProfileRules` (regole pure RB-06/07/08, testabili senza Spring), `ProfileService` (stato COMPLETO/NON_PIU_COMPLETO/ASSENTE, salvataggio tutto-o-niente, 409 NESSUN_DATO), `ProfileController`, `AccessController`.
- `analysis`: `NearestRange` (utility generica e pura, usata per RB-07 e RB-09/10), `ComparisonService` (metodi statici puri `compare`/`chooseBand`/`roundEuro` + orchestrazione), `AnalysisController`.

Criteri di accettazione backend coperti (con test dedicati):
- CA-01 accesso (`AccessControllerTest`: campo mancante, troppo lungo, normalizzazione " Marco "→"marco").
- CA-02, CA-14 stato profilo e 409 NESSUN_DATO (`ProfileService`, verificato indirettamente da `ExampleFilesTest`/`ComparisonServiceTest`).
- CA-04 validazione profilo (`ProfileRulesTest`: zeri iniziali, spazi, importo oltre limite, negativo, non-cifre, mancante).
- CA-05, CA-06, CA-07, CA-08 confronto e soglie (`ComparisonServiceTest`: Auto 6.200/5.000→SOPRA 24% SOPRA_PERCENTUALE; soglie Utenze 5.500/5.501/4.500/4.499/5.000; totale Marco 33.600/33.000→IN_LINEA 2% IN_LINEA_PIU; campione zero; arrotondamento ,50).
- CA-09 fascia approssimata e CA-10 punteggio/risposte (`NearestRangeTest`: dentro/vicino/parità minimo più basso; `ExampleFilesTest`: profilo Marco → Z=15, fascia F3 14–18, non approssimato; profilo fuori fascia → Z=21, fascia F4 23–25, approssimato).
- CA-11, CA-12 import CSV (`CsvParserTest`: riga valida, campo mancante, voce sconosciuta, decimale con virgola (4200,50→4201), importo negativo, fascia sovrapposta, fascia incompleta, file vuoto, intestazione non valida, solo intestazione; `esempioValido` 66/66/0 con 9 domande e 4 fasce; `esempioErrori` 79/66/13 con i 13 codici di scarto attesi riga per riga).
- CA-13 è coperto per costruzione (413 via `spring.servlet.multipart.max-file-size=2MB` + `GlobalExceptionHandler`; FILE_NON_CSV/FILE_VUOTO/FILE_INTESTAZIONE_NON_VALIDA via `CsvParserTest`); va riverificato dal tester a livello HTTP (upload reale) perché non è coperto da test `@SpringBootTest` dedicati, per limiti di tempo.

File principali: `app/backend/pom.xml`, `app/backend/src/main/resources/application.properties`, `app/backend/src/main/java/it/hagenthon/backend/**`, `app/backend/src/test/java/it/hagenthon/backend/**`, `app/backend/src/test/resources/esempio_istat.csv`, `app/backend/src/test/resources/esempio_istat_errori.csv`.

## Frontend
_(aggiornato da dev-frontend)_

Progetto Angular standalone creato in `app/frontend/` (F-01…F-10 completati). `npx ng build` verde.

**Schermate completate**
- S-01 Login (`pages/login/login-page.component.ts`) – CA-01
- S-02 Conferma accesso / MFA simulata, conto alla rovescia 5s (`pages/confirm/confirm-page.component.ts`) – CA-02
- Intestazione + menu hamburger, Esc che chiude il menu senza arrivare alla ragnatela, Esci (`layout/shell.component.ts`) – CA-03
- S-05 Amministrazione, caricamento CSV, tabella scarti (`pages/admin/admin-page.component.ts`) – CA-11, CA-12, CA-13, CA-14
- S-04 Profilo, questionario dinamico + 5 importi, errori per campo + riepilogo (`pages/profile/profile-page.component.ts`) – CA-02, CA-04, CA-14
- S-03 Analisi di spesa + ragnatela SVG scritta a mano (`pages/analysis/analysis-page.component.ts`, `pages/analysis/radar-chart.component.ts`) – CA-05, CA-06, CA-07, CA-08, CA-09, CA-10
- `shared/no-data.component.ts` per RB-25 (CA-14)

**File infrastrutturali**: `testi.ts` (tutti i testi + mappe codice→testo), `format.ts`, `api.models.ts`, `api.service.ts` (timeout 10s su tutte le chiamate, 1 retry solo sulle GET per stato 0/timeout/≥500), `session.service.ts`, `auth.guard.ts`, `app.routes.ts`, `proxy.conf.json` collegato in `angular.json` (`serve.options.proxyConfig`).

**Non verificato con backend reale** (il backend non era ancora avviabile durante lo sviluppo): solo `ng build` è stato eseguito, come richiesto. Nessun calcolo di business nel frontend: tutti i valori mostrati arrivano dal backend.

**Fase 4 – Revisione conformità, ciclo 1**: RC-01 e RC-02 corretti in `testi.ts` (`profilo.riepilogoErrori` con singolare/plurale; `profilo.aiutoImporto` reso più sintetico). RC-03 corretto: testo "Profilo" spostato da `profile-page.component.ts` a `testi.ts` (`profilo.titoloMenuProfilo`). `npx ng build` verde dopo le correzioni. Nessuna contestazione.

**RB-26 "Dove verificare"** (modifica approvata dopo la consegna iniziale): aggiunto in `testi.ts` (`doveVerificare`, `linkVerificaPerVoce`, tipo `LinkVerifica`) e in `pages/analysis/analysis-page.component.ts`. Il riquadro compare nel box di S-03 sotto "Da sapere" solo quando la voce mostrata (mai il totale) ha esito `SOPRA`; collegamenti MIMIT/IVASS (Auto e mobilità), ARERA (Utenze), Banca d'Italia (Casa, Sport e tempo libero, Spesa) con `target="_blank" rel="noopener noreferrer"` e dichiarazione a parole "Si apre in una nuova scheda."; chiusura fissa "Sono servizi pubblici e gratuiti. La scelta resta tua." Nome app aggiornato a "ContiInTasca" (già presente in `testi.ts`). `npx ng build` verde.

**Restyling rapido (solo CSS)**: riscritto `src/styles.css` con nuova palette viola (#7500C0/#A100FF), sfondo #F6F4FA, card bianche arrotondate con ombra, pulsanti/campi/tabelle coerenti, badge per l'avviso demo, blocco con bordo sinistro viola per "Da sapere"/"Dove verificare". Aggiunta solo markup di wrapping (classi `card`, `pagina-centrata`, `area-upload`, `blocco-nota`, `importo-grande`) in `shell.component.ts`, `login-page.component.ts`, `confirm-page.component.ts`, `admin-page.component.ts`, `profile-page.component.ts`, `analysis-page.component.ts`; nessun testo, binding, logica o routing modificato. Ragnatela: poligono "la tua spesa" ora viola pieno semitrasparente, "spesa campione" grigio tratteggiato, ramo selezionato evidenziato in viola. Intestazione sticky bianca con ombra, nome app in viola, voci di menu con hover viola chiaro. Responsive: sotto 900px le colonne di Analisi di spesa vanno in verticale (invariato, già presente). `npx ng build` verde al primo tentativo.

**Problema aperto non toccato in questo turno (fuori perimetro CSS-only)**: leggendo `confirm-page.component.ts` per applicare lo stile, ho notato che `confermaAccesso()` ora naviga sempre a `/profilo` senza più chiamare `GET /api/profilo` per instradare a `/analisi` quando lo stato è `COMPLETO`, diversamente da quanto consegnato inizialmente (vedi CA-02). Non l'ho corretto per rispettare il vincolo "solo CSS" di questo ciclo; segnalo per un eventuale ciclo di correzione dedicato.
