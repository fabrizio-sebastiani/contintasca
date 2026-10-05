# Piano tecnico – "ContiInTasca"

> Versione 1 (Fase 1). Input: `agents/docs/requisiti-funzionali.md` v2 (approvati), `agents/docs/contratto-api.md`, `agents/skills/formato-csv-istat/SKILL.md`.
> Obiettivo: `dev-backend` e `dev-frontend` lavorano **in parallelo, senza parlarsi**, in circa 30 minuti. Il punto d'incontro è solo il contratto API.

**Principi**
- Ogni regola di business vive in **un solo punto**. Calcoli, validazione dei valori e scelta delle frasi stanno nel **backend**; il frontend mostra dati, formatta numeri e sceglie i testi in base ai codici.
- Niente Docker, niente librerie oltre a quelle elencate. Login e MFA sono **simulati**: nessuna libreria di sicurezza, nessuna password salvata o scritta nei log.
- Il frontend può essere sviluppato senza backend avviato: il contratto contiene tutti i JSON di esempio.

---

## 1. Stack e versioni

| Parte | Scelta |
|---|---|
| Backend | Spring Boot stabile corrente da start.spring.io (comando in `agents/skills/verifica-build/SKILL.md`), Maven, `javaVersion=21` (`<java.version>21</java.version>`), eseguito con il JDK 25 installato. Dipendenze **solo**: `web`, `data-jpa`, `h2`, `validation` (+ i test starter che start.spring.io aggiunge da solo). |
| Frontend | Angular stabile corrente (`npx @angular/cli@latest new`), componenti **standalone**, form reattivi, `HttpClient`, router. Stato dei componenti con **signals** (funziona sia con zone.js sia in modalità zoneless). CSS semplice, nessuna libreria UI né di grafici: la ragnatela è **SVG scritto a mano**. |
| Database | H2 su file. |
| Codice | Nomi di classi, metodi, endpoint in inglese o come nel contratto; documentazione e testi in italiano. |

Note di compatibilità per il backend (Spring Boot 4.x): i DTO di risposta usano `record` Java e le date sono **stringhe già formattate** (`yyyy-MM-ddTHH:mm:ss`), così il formato non dipende dalla versione di Jackson. Per i test usare `@SpringBootTest` o test unitari puri (niente `@DataJpaTest`/`@WebMvcTest`, che in Boot 4 richiedono moduli aggiuntivi). I nomi delle classi di test finiscono con `Test` (Surefire non esegue le classi `*IT`).

## 2. Backend (`app/backend/`)

Package radice: `it.hagenthon.backend`.

### 2.1 Package e classi
| Package | Classi | Responsabilità |
|---|---|---|
| `config` | `WebConfig` | CORS su `/api/**` solo da `http://localhost:4200`, metodi GET, POST, PUT |
| `common` | `ApiError` (record: codice, messaggio, dettagli), `ErrorDetail` (record: campo, codice, minimo, massimo), `ApiException` (status HTTP, codice, dettagli), `GlobalExceptionHandler` (`@RestControllerAdvice`), `UserNames` (normalizzazione e controllo del nome utente), `Timestamps` (formato data) | formato d'errore unico, regole comuni |
| `data` | entità `Question`, `AnswerOption`, `AnswerRange`, `ScoreBand`, `SampleAmount`, `DataLoad`; enum `QuestionType` (`SCELTA`, `NUMERO`), `Voce` (5 codici in ordine RB-15); repository Spring Data; `CsvParser`; `DataImportService`; `DataController` | file dati: lettura, validazione, sostituzione |
| `profile` | entità `Profile`; `ProfileRepository`; `ProfileRules`; `ProfileService`; `ProfileController`; `AccessController` | accesso simulato, profilo, validazione delle risposte |
| `analysis` | `NearestRange` (utility), `ComparisonService`, `AnalysisController` | punteggio, fascia, confronto |

Controller **sottili**: leggono la richiesta, chiamano un servizio, restituiscono il DTO. Nessuna query SQL scritta a mano: solo metodi derivati di Spring Data.

### 2.2 Entità (tabelle H2)
Tutte con `@Id @GeneratedValue Long id`. Vincoli `jakarta.validation` come rete di sicurezza (la validazione con i codici la fanno servizi e parser).

| Entità (tabella) | Campi e vincoli |
|---|---|
| `Question` (`question`) | `codice` String ≤30, unico, non null; `tipo` `QuestionType` (`@Enumerated(STRING)`); `etichetta` ≤40 non null; `testo` ≤200 non null; `spiegazione` ≤200 nullable; `ordine` int (posizione nel file) |
| `AnswerOption` (`answer_option`) | `question` `@ManyToOne` non null; `codice` ≤30; `testo` ≤100; `punteggio` int ≥0; `ordine` int |
| `AnswerRange` (`answer_range`) | `question` `@ManyToOne` non null; `minimo` long ≥0; `massimo` long ≥ minimo; `punteggio` int ≥0 |
| `ScoreBand` (`score_band`) | `codice` ≤30 unico; `minimo` int ≥0; `massimo` int ≥ minimo |
| `SampleAmount` (`sample_amount`) | `band` `@ManyToOne` non null; `voce` `Voce` (STRING); `importo` `BigDecimal(precision 8, scale 2)` ≥0 (valore del file, non arrotondato) |
| `DataLoad` (`data_load`) | `nomeFile` ≤255; `caricatoIl` `LocalDateTime`; `numeroDomande` int; `numeroFasce` int. Al massimo una riga. |
| `Profile` (`profile`) | `nomeUtente` ≤50, unico, già normalizzato; `risposte` `@ElementCollection Map<String,String>` (tabella `profile_answer`, colonne `codice_domanda`, `valore` ≤20); `casa`, `sportTempoLibero`, `autoMobilita`, `utenze`, `spesa` int 0..999999; `salvatoIl` `LocalDateTime` |

I profili **non** si cancellano a un nuovo caricamento: restano e possono diventare `NON_PIU_COMPLETO`.

### 2.3 Servizi e dove vive ciascuna RB
| RB | Dove (unico punto) | Note di realizzazione |
|---|---|---|
| RB-01 | `UserNames.normalize()` + `AccessController` | trim + `toLowerCase(Locale.ROOT)`; controlli 1–50 / 1–100 con codici `CAMPO_MANCANTE`, `CAMPO_TROPPO_LUNGO`. La password si legge, si controlla la lunghezza e si scarta. |
| RB-02 | FE `ConfirmPageComponent` | – |
| RB-03 | FE `ConfirmPageComponent` (sceglie la pagina in base a `statoProfilo` del backend) | – |
| RB-04 | FE `SessionService` + `authGuard` | – |
| RB-05 | `ProfileService.stato()` | `COMPLETO` se esiste il profilo e ogni domanda attuale ha una risposta valida (gli importi salvati sono sempre validi) |
| RB-06 | `ProfileRules.validateAmount()` | regex esplicite `[0-9]+` e `-[0-9]+`; zeri iniziali tolti prima del confronto con 999999; stringhe lunghe → `IMPORTO_OLTRE_LIMITE` senza parse (niente overflow) |
| RB-07 | `ProfileRules.allowedRange()` (min–max ammessi) e `ProfileRules.rangeFor()` (valore in un vuoto → `NearestRange`) | |
| RB-08 | `ProfileRules.score()` | punteggio dell'opzione o della fascia di risposta |
| RB-09, RB-10 | `ComparisonService.chooseBand()` con `NearestRange.pick()` | `NearestRange.pick(intervalli, valore)`: intervallo che contiene il valore; altrimenti distanza minima dall'estremo più vicino; a parità, minimo più basso. **La stessa utility serve RB-07 e RB-10.** |
| RB-11 | `ComparisonService` | `importo.setScale(0, RoundingMode.HALF_UP)` prima di ogni calcolo; totale = somma dei 5 arrotondati |
| RB-12 | `ComparisonService.compare()` | `percentuale = BigDecimal(|d| × 100).divide(campione, 0, HALF_UP)`; `null` se campione = 0 |
| RB-13 | `ComparisonService.compare()` | solo aritmetica intera: `IN_LINEA` se `d == 0` oppure `|d| × 10 ≤ campione`; altrimenti `SOPRA` se `d > 0`, `SOTTO` se `d < 0` |
| RB-14 | FE `format.ts` | – |
| RB-15 | BE `scalaMassima`; FE `RadarChartComponent` (disegno) | |
| RB-16 | FE `AnalysisPageComponent` (stato `voceMostrata`) | – |
| RB-17 | BE `ComparisonService.phrase()` sceglie `fraseCifra`; FE `testi.ts` contiene le tabelle A, B, C | regole di `fraseCifra`: campione 0 e d > 0 → `SOPRA_CAMPIONE_ZERO`; SOPRA → `P == 10 ? SOPRA_OLTRE_10 : SOPRA_PERCENTUALE`; SOTTO analogo; IN_LINEA → `UGUALE` / `PIU` / `MENO` secondo il segno di d |
| RB-18 | BE `AnalysisController` (risposte con etichetta e testo/numero); FE formatta | |
| RB-19 | FE `testi.ts` | – |
| RB-20, RB-21 | `CsvParser` | regole e ordine dei controlli **alla lettera** da `formato-csv-istat` |
| RB-22 | `DataImportService.importFile()` `@Transactional` | prima si legge e valida **tutto in memoria**; solo se il file è accettato si cancellano i vecchi dati (`deleteAllInBatch` nell'ordine figli → padri) e si salvano i nuovi, nella stessa transazione |
| RB-23 | file di esempio in `agents/skills/formato-csv-istat/` | copiati in `src/test/resources/` per i test |
| RB-24 | `ProfileService.get()` (stato, risposte ancora valide); FE `ProfilePageComponent` (avvisi) | |
| RB-25 | BE `409 NESSUN_DATO` da `ProfileService` e `ComparisonService`; FE `NoDataComponent` | |

### 2.4 Controller (vedi contratto per i JSON)
| Controller | Endpoint |
|---|---|
| `AccessController` | `POST /api/accesso` |
| `DataController` | `GET /api/dati`, `POST /api/dati` (`@RequestParam("file") MultipartFile`, passa al servizio nome originale e byte) |
| `ProfileController` | `GET /api/profilo`, `PUT /api/profilo` (`@RequestParam String nomeUtente`) |
| `AnalysisController` | `GET /api/analisi` |

Log SLF4J a livello INFO **solo** per: import completato (righe lette/importate/scartate), import rifiutato (codice). Mai password, nome utente, importi o contenuto del file.

## 3. Frontend (`app/frontend/`)

### 3.1 File
| Percorso (`src/app/`) | Contenuto |
|---|---|
| `testi.ts` | **tutti** i testi visibili: nome app, titoli, etichette, aiuti, avvisi, pulsanti, voci del menu; nomi delle voci (`CASA` → "Casa", …); tabelle A, B (modelli con `{D}` e `{P}`), C di RB-17; Parole utili (RB-19); frase sulla media; disclaimer e fonte (testo esatto, sezione 7 dei requisiti); mappa `codice → testo` per ogni codice delle sezioni 4.F, 4.G, 4.H del contratto (errori in tre parti). Nessun componente contiene frasi nel template. |
| `format.ts` | `euro(n)` → `12.000 €` (punto delle migliaia con regex, **non** `toLocaleString`, che per l'italiano può non mettere il punto nei numeri di 4 cifre); `numero(n)` → `28.000`; `percentuale(n)` → `24%`; `dataOra(iso)` → `05/10/2026 alle 10:15`; `riempi(modello, valori)` per i segnaposto `{D}`, `{P}`, `{X}`, `{Y}`, `{Z}`, `{N}`, `{MAX}` |
| `api.models.ts` | interfacce TypeScript copiate dai JSON del contratto |
| `api.service.ts` | unico servizio HTTP (sezione 5) |
| `session.service.ts` | `pendingUser` (in memoria, tra S-01 e S-02); `user` in `sessionStorage` chiave `itm.nomeUtente`; `login(nome)`, `logout()`, `isLogged()` |
| `auth.guard.ts` | `CanActivateFn`: se non c'è l'accesso → `/accesso` (RB-04) |
| `app.routes.ts` | vedi 3.3 |
| `pages/login/login-page.component.ts` | S-01 |
| `pages/confirm/confirm-page.component.ts` | S-02 |
| `layout/shell.component.ts` | intestazione con nome app e menu hamburger + `<router-outlet>` per S-03, S-04, S-05 |
| `pages/analysis/analysis-page.component.ts` | S-03: layout, box, sezioni sotto |
| `pages/analysis/radar-chart.component.ts` | ragnatela SVG |
| `pages/profile/profile-page.component.ts` | S-04 |
| `pages/admin/admin-page.component.ts` | S-05 |
| `shared/no-data.component.ts` | messaggio RB-25 con collegamento a `/amministrazione` |
| `proxy.conf.json` (radice del progetto) | `{ "/api": { "target": "http://localhost:8080", "secure": false } }`, collegato in `angular.json` → `serve.options.proxyConfig` |

### 3.2 Schermate
| Schermata | Componente | Comportamento |
|---|---|---|
| S-01 `/accesso` | `LoginPageComponent` | Avviso dimostrativo sempre visibile. "Accedi" → `POST /api/accesso`; `400 VALIDAZIONE` → messaggio accanto al campo da `dettagli`; `200` → `session.pendingUser = nomeUtente` restituito → `/conferma`. Campo password `type="password"`; la password non resta nello stato dopo l'invio. |
| S-02 `/conferma` | `ConfirmPageComponent` | Se `pendingUser` manca → `/accesso`. Conto alla rovescia 5…1 con un timer da 1 s (signal); a 5 s: `session.login(pendingUser)`, poi `GET /api/profilo`: `COMPLETO` → `/analisi`; ogni altro esito (anche `409 NESSUN_DATO` o errore) → `/profilo`. "Annulla" ferma il timer, svuota `pendingUser` → `/accesso`. Timer cancellato in `ngOnDestroy`. |
| Intestazione | `ShellComponent` | Pulsante `<button aria-expanded aria-controls aria-label="Menu">`; voci Profilo, Analisi di spesa, Amministrazione (con `routerLinkActive` + `aria-current="page"`), Esci (`session.logout()` → `/accesso`). Esc chiude il menu (se aperto, l'Esc non arriva alla ragnatela). Scegliere una voce chiude il menu. |
| S-03 `/analisi` | `AnalysisPageComponent` + `RadarChartComponent` | `GET /api/analisi`. Errori: `NESSUN_DATO` → `NoDataComponent`; `PROFILO_ASSENTE` → `/profilo?avviso=compila`; `PROFILO_NON_PIU_COMPLETO` → `/profilo`. Layout a griglia: due colonne da 900 px in su, una colonna sotto. Box con `aria-live="polite"`: titolo, "Spesa corrente", "Spesa campione", "Da sapere" (3 frasi: A[voce], B[`fraseCifra`] riempita con D e P, C[voce][esito]); per il totale il modello B inizia con "In totale" invece di "Per <Voce>". Pulsante "Vedi i totali". Sotto, sempre visibili: "Con chi ti confronti" (frase con X, Y, Z + una riga per risposta "<etichetta>: <testoOpzione o numero formattato>"), avviso RB-10 se `approssimato`, Parole utili, frase sulla media, disclaimer, fonte. |
| Ragnatela | `RadarChartComponent` | Input: `voci`, `scalaMassima`, `voceMostrata`; output: `seleziona(voce)`. SVG `viewBox="0 0 520 440"`, centro (260, 220), raggio 150; ramo i all'angolo −90° + i·72° (Casa in alto, poi in senso orario). Raggio del punto = 150 × valore ÷ scala (se `scalaMassima` = 0 usare 1). Griglia di 4 pentagoni concentrici. Poligono "La tua spesa" linea continua, "Spesa campione" tratteggiata (`stroke-dasharray`), colori diversi. Ogni ramo è un `<g tabindex="0" role="button" [attr.aria-pressed]>` nell'ordine RB-15 con linea, area di clic larga trasparente ed etichetta "Nome voce" + i due importi; eventi `mouseenter`, `focus`, `click`, `keydown.enter`, `keydown.space` (con `preventDefault`) → `seleziona`. Ramo mostrato evidenziato; focus visibile (`outline`). Legenda HTML sotto l'SVG. |
| S-04 `/profilo` | `ProfilePageComponent` | `GET /api/profilo`. `409 NESSUN_DATO` → `NoDataComponent`, nessun questionario. Avviso in alto: `NON_PIU_COMPLETO` → "Le domande del profilo sono cambiate…"; `ASSENTE` **e** `?avviso=compila` → "Per vedere l'analisi di spesa compila prima il tuo profilo."; altrimenti nessun avviso. Parte A: per ogni domanda `testo` come label, `spiegazione` sotto; `SCELTA` → radio se ≤ 4 opzioni, `<select>` se di più; `NUMERO` → `<input type="text" inputmode="numeric">`. Parte B: 5 campi `type="text" inputmode="numeric"` (mai `type="number"`: deve arrivare al backend "12.000" così com'è) con frase "cosa include" e aiuto. Precompilazione da `risposte` e `importi`. Il form reattivo **non ha validatori**: "Salva e vedi l'analisi" → `PUT /api/profilo` con tutti i valori come stringhe; `400 VALIDAZIONE` → messaggio accanto a ogni campo di `dettagli` (`aria-describedby`, `aria-invalid`) e riepilogo "Ci sono N campi da correggere." con N = `dettagli.length`, focus sul riepilogo; `200` → `/analisi`. |
| S-05 `/amministrazione` | `AdminPageComponent` | Testo fisso sui dati inventati. "Dati attuali" da `GET /api/dati` (data con `dataOra`). `<input type="file" accept=".csv">` + "Carica": nessun file scelto → testo di `FILE_MANCANTE` senza chiamata; altrimenti `POST /api/dati` (`FormData`, parte `file`). `200` → "Caricamento completato.", righe lette/importate/scartate, tabella "Riga / Motivo" (motivo = testo di `scarti[].codice`), aggiorna "Dati attuali" con `datiAttuali`. Errore → messaggio in tre parti del codice; "Dati attuali" invariato. |

### 3.3 Routing
```
''                 → redirect 'accesso' (pathMatch full)
'accesso'          → LoginPageComponent
'conferma'         → ConfirmPageComponent
''  (ShellComponent, canActivateChild: [authGuard])
    'analisi'          → AnalysisPageComponent
    'profilo'          → ProfilePageComponent
    'amministrazione'  → AdminPageComponent
'**'               → redirect 'accesso'
```
L'accesso vale per la scheda perché sta in `sessionStorage` (resiste al ricaricamento, sparisce chiudendo la scheda; una scheda nuova non lo ha).

## 4. Configurazione

`app/backend/src/main/resources/application.properties`:
```
server.port=8080
spring.datasource.url=jdbc:h2:file:./data/hagenthon-db
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.h2.console.enabled=false
spring.servlet.multipart.max-file-size=2MB
spring.servlet.multipart.max-request-size=3MB
server.tomcat.max-swallow-size=10MB
server.error.include-stacktrace=never
server.error.include-message=never
```
- `./data/` è relativo a `app/backend/` (cartella di avvio): aggiungere `data/` al `.gitignore` del backend.
- `max-swallow-size` più alto del limite serve a far arrivare al browser il `413 FILE_TROPPO_GRANDE` invece di una connessione chiusa.
- Test: `src/test/resources/application.properties` con `spring.datasource.url=jdbc:h2:mem:testdb` per non toccare il file di sviluppo.
- Console H2 disattivata; CORS solo `http://localhost:4200`; frontend su porta 4200 (`npx ng serve`), backend su 8080.

## 5. Gestione errori e robustezza

**Backend** – un solo `GlobalExceptionHandler` produce sempre il formato del contratto:
| Eccezione | HTTP / codice |
|---|---|
| `ApiException` | quelli dell'eccezione |
| `MaxUploadSizeExceededException` | 413 `FILE_TROPPO_GRANDE` |
| `MissingServletRequestPartException`, `MultipartException` | 400 `FILE_MANCANTE` |
| `MissingServletRequestParameterException`, `HttpMessageNotReadableException`, `MethodArgumentTypeMismatchException` | 400 `RICHIESTA_NON_VALIDA` |
| `NoResourceFoundException` | 404 `RISORSA_NON_TROVATA` |
| `HttpRequestMethodNotSupportedException` | 405 `METODO_NON_AMMESSO` |
| qualsiasi altra `Exception` | 500 `ERRORE_INTERNO` (log ERROR con il solo tipo di eccezione e lo stack, mai il corpo della richiesta) |

Validazione degli input: tutti i parametri arrivano come stringhe e passano da `UserNames`, `ProfileRules` o `CsvParser`; nessun valore dell'utente entra in query costruite a mano.

**Frontend** – in `ApiService`:
- `timeout(10000)` su **tutte** le chiamate;
- `retry({ count: 1 })` **solo** sulle GET e solo se l'errore è di rete (stato 0), timeout o stato ≥ 500;
- ogni errore diventa `{ codice, dettagli }`: dal corpo del server se presente, `RETE_NON_DISPONIBILE` per stato 0 o timeout, `ERRORE_INTERNO` se il corpo non ha `codice`. I componenti mostrano **solo** testi di `testi.ts`, mai `messaggio` del server.
- Valori dal file dati (etichette, testi, nome file) mostrati con l'interpolazione di Angular (mai `innerHTML`).

## 6. Tracciabilità

| CA | Backend | Frontend | Test |
|---|---|---|---|
| CA-01 | `AccessController`, `UserNames` | `LoginPageComponent` | `AccessControllerTest`: nome vuoto → `CAMPO_MANCANTE`; 51 caratteri → `CAMPO_TROPPO_LUNGO` 50; " Marco " → "marco". Tester: passi CA-01 |
| CA-02 | `ProfileService.stato()` | `ConfirmPageComponent`, `ProfilePageComponent` | `ProfileServiceTest.statoCompletoConMaiuscoleDiverse`; tester: attesa 5 s, nome nuovo / esistente |
| CA-03 | – | `authGuard`, `SessionService`, `ShellComponent` (Esci) | tester: URL diretti, ricarica, Esci |
| CA-04 | `ProfileRules`, `ProfileService.save()` | `ProfilePageComponent` | `ProfileServiceTest.treErroriNessunSalvataggio` (-100, 12.000, vuoto); `ProfileRulesTest` (zeri iniziali, spazi, 1000000) |
| CA-05 | `ComparisonService` (`scalaMassima`) | `RadarChartComponent` | `ComparisonServiceTest.scalaMassima`; tester: ordine rami, legenda, importi |
| CA-06 | `ComparisonService` | `AnalysisPageComponent` | `ComparisonServiceTest.autoSopra24` (6200/5000 → SOPRA, 24, `SOPRA_PERCENTUALE`); tester: mouse, Tab, clic |
| CA-07 | `ComparisonService` (totale) | `AnalysisPageComponent` | `ComparisonServiceTest.totale`; tester: apertura, Esc, "Vedi i totali" |
| CA-08 | `ComparisonService` | `testi.ts` | `ComparisonServiceTest.soglieUtenze` (5500 IN_LINEA_PIU, 5501 SOPRA_OLTRE_10, 4500 IN_LINEA_MENO, 4499 SOTTO_OLTRE_10, 5000 IN_LINEA_UGUALE); campione 0 |
| CA-09 | `NearestRange`, `ComparisonService` | `AnalysisPageComponent` (avviso) | `NearestRangeTest` (dentro, vicino, parità → più basso); `ExampleFilesTest`: profilo fuori fascia → Z 21, fascia 23–25, `approssimato` |
| CA-10 | `AnalysisController` (risposte) | `AnalysisPageComponent`, `testi.ts` | `ExampleFilesTest`: Marco → Z 15, fascia 14–18; tester + `revisore-conformita` sui testi |
| CA-11 | `CsvParser`, `DataImportService` | `AdminPageComponent` | `CsvParserTest.esempioValido` (66 lette, 66 importate, 0 scartate, 9 domande, 4 fasce) |
| CA-12 | `CsvParser` | `AdminPageComponent` | `CsvParserTest.esempioErrori` (79 / 66 / 13, righe e codici come nella skill) |
| CA-13 | `DataImportService`, `GlobalExceptionHandler` | `AdminPageComponent` | `DataImportServiceTest`: .txt, vuoto, intestazione sbagliata → dati invariati; tester: file > 2 MB → 413 |
| CA-14 | `ProfileService`, `ComparisonService` (409 `NESSUN_DATO`) | `NoDataComponent` | `ProfileServiceTest.nessunDato`; tester: database vuoto |

Test JUnit minimi richiesti a `dev-backend` anche per il parser: riga valida, campo mancante, voce sconosciuta, decimale con virgola (`4200,50` → arrotondato 4201), importo negativo, fascia sovrapposta, fascia incompleta.

## 7. Task

I due elenchi sono indipendenti: nessuna task di un dev aspetta l'altro. Tempo indicativo totale: 30 minuti ciascuno.

### 7.1 `dev-backend` (in ordine)
| # | Task | CA |
|---|---|---|
| B-01 | Crea il progetto (`verifica-build`), `application.properties` (sezione 4), properties di test con H2 in memoria, `data/` nel `.gitignore`. | – |
| B-02 | `common`: `ApiError`, `ErrorDetail`, `ApiException`, `GlobalExceptionHandler` (tabella sezione 5), `UserNames`, `Timestamps`; `config.WebConfig` (CORS). | CA-13 (413) |
| B-03 | Entità ed enum (sezione 2.2) e repository Spring Data. | – |
| B-04 | `CsvParser`: intestazione, codifica, 13 colonne, controlli di riga nell'**ordine della skill**, controlli finali (`FASCIA_INCOMPLETA`, `DOMANDA_SENZA_RISPOSTE`), conteggi. Restituisce un risultato in memoria (domande, opzioni, fasce, importi, scarti) senza toccare il DB. | CA-11, CA-12 |
| B-05 | `DataImportService` (controlli sul file, RB-22, sostituzione transazionale, `DataLoad`) e `DataController` (`GET`/`POST /api/dati`). | CA-11, CA-12, CA-13 |
| B-06 | `AccessController` (`POST /api/accesso`). | CA-01 |
| B-07 | `NearestRange`, `ProfileRules` (RB-06, RB-07, RB-08), `ProfileService` (stato, risposte ancora valide, salvataggio tutto-o-niente, `409 NESSUN_DATO`), `ProfileController`. | CA-02, CA-04, CA-14 |
| B-08 | `ComparisonService` (RB-09…RB-13, `fraseCifra`, totale, `scalaMassima`, 409) e `AnalysisController` (con `risposte` per RB-18). | CA-05 … CA-10 |
| B-09 | Copia `esempio_istat.csv` ed `esempio_istat_errori.csv` da `agents/skills/formato-csv-istat/` in `src/test/resources/`; test della sezione 6; `mvn -q test` verde. | tutti i CA backend |

### 7.2 `dev-frontend` (in ordine)
| # | Task | CA |
|---|---|---|
| F-01 | Crea il progetto (`verifica-build`), `proxy.conf.json` + `angular.json`, `provideHttpClient()` in `app.config.ts`, stile di base (font leggibile, contrasto, focus visibile). Sostituisci o elimina lo spec di default che controlla il titolo generato. | – |
| F-02 | `api.models.ts`, `api.service.ts` (sezione 5), `format.ts`, `session.service.ts`, `auth.guard.ts`, `app.routes.ts`. | CA-03 |
| F-03 | `testi.ts` completo (sezione 3.1), rispettando `linguaggio-semplice` e i testi esatti dei requisiti. | CA-10 |
| F-04 | S-01 `LoginPageComponent`. | CA-01 |
| F-05 | S-02 `ConfirmPageComponent`. | CA-02 |
| F-06 | `ShellComponent` con menu hamburger ed Esci. | CA-03 |
| F-07 | `NoDataComponent` e S-05 `AdminPageComponent`. | CA-11, CA-12, CA-13, CA-14 |
| F-08 | S-04 `ProfilePageComponent`. | CA-02, CA-04, CA-14 |
| F-09 | S-03 `AnalysisPageComponent` + `RadarChartComponent`. | CA-05, CA-06, CA-07, CA-08, CA-09, CA-10 |
| F-10 | `npx ng build` verde. | – |
