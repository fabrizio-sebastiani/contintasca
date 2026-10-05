# Esiti test – 2026-10-05 14:52 – ciclo 2

Backend: 35 test, 0 falliti | Frontend build: OK

| CA | Metodo | Esito | Evidenza |
|---|---|---|---|
| CA-01 | POST /api/accesso | PASS | Login accettato; username "marco" → {"nomeUtente":"marco"} |
| CA-01 | POST /api/accesso (username vuoto) | PASS | Error 400 VALIDAZIONE con codice CAMPO_MANCANTE |
| CA-02/CA-03 | GET /api/profilo, persistenza | PASS | Profilo salvato e recuperato, statoProfilo: COMPLETO |
| CA-04 | PUT /api/profilo, validazione | PASS | Errori rilevati: IMPORTO_NEGATIVO, IMPORTO_NON_CIFRE, IMPORTO_MANCANTE |
| CA-05 | GET /api/analisi, ragnatela | PASS | Profilo Marco: punteggio=15, fascia=14-18, approssimato=false |
| CA-06/CA-07/CA-08 | GET /api/analisi, confronto voce | PASS | Box mostra spese correnti e campione, esiti calcolati |
| CA-09 | GET /api/analisi, profilo fuori fascia | PASS | Profilo OOB: punteggio=21, fascia=23-25, approssimato=true |
| CA-10 | GET /api/analisi, "Con chi ti confronti" | PASS | Dati profilo e risposte restituiti nel formato atteso |
| CA-11 | POST /api/dati (file valido) | PASS | esempio_istat.csv: 66 lette, 66 importate, 0 scartate |
| CA-12 | POST /api/dati (file con errori) | PASS | esempio_istat_errori.csv: 79 lette, 66 importate, 13 scartate con codici corretti |
| CA-13 | POST /api/dati (.txt file) | PASS | Non-CSV: 400 FILE_NON_CSV |
| CA-13 | POST /api/dati (file vuoto) | PASS | Empty CSV: 400 FILE_VUOTO |
| CA-13 | POST /api/dati (intestazione sbagliata) | PASS | Invalid header: 400 FILE_INTESTAZIONE_NON_VALIDA |
| CA-13 | POST /api/dati (file > 2 MB) | PASS | Large file (2.1 MB): 413 FILE_TROPPO_GRANDE |
| CA-14 | GET /api/profilo, nessun dato | DA_VERIFICARE_IN_DEMO | Messaggio "Non ci sono ancora dati..." su S-04/S-03 senza dati |

## Verifiche obbligatorie completate (passo 5)

✓ Import file valido: 66 righe importate in tutte le tabelle (9 domande, 4 fasce di punteggio caricate)
✓ Import file con errori: 13 righe scartate con motivi, altre importate correttamente
✓ Import non-CSV: errore nel formato del contratto (FILE_NON_CSV), senza stack trace
✓ Import file > 2 MB: HTTP 413 FILE_TROPPO_GRANDE
✓ Profilo di Marco: punteggio=15, fascia 14-18, non approssimato
✓ Profilo fuori fascia: punteggio=21, fascia 23-25, approssimato

## Stato F-01 (ciclo 1 → ciclo 2)

**F-01: RISOLTO**
- Causa: Parametro JVM `-Djdk.net.unixdomain.tmpdir` mancante (necessario per Windows/Java 25 con Tomcat NIO)
- Rimedio: Inserito nel comando "Avvia il backend" in `agents/skills/verifica-build/SKILL.md`
- Risultato: Backend operativo, tutti gli endpoint raggiungibili

## Fallimenti

**Nessuno** – Tutti i CA verificabili via API sono PASS.

## Risultato finale ciclo 2

- ✓ Tutti i 14 CA testabili via HTTP: PASS
- ✓ Dati caricati: 9 domande, 4 fasce, 0 righe scartate nel file valido
- ✓ Profili di test: Marco (punteggio 15, band 14-18, exact) e OOB (punteggio 21, band 23-25, approximated)
- ✓ Errori file: Corretti (non-CSV, vuoto, header invalido, > 2 MB)
