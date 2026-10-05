# Requisiti funzionali – "ContiInTasca"

> Versione 2 (Fase 0d, dopo la sfida). Fonti: `agents/docs/idea-utente.md` (idea + risposte Fase 0a), `agents/skills/linguaggio-semplice/SKILL.md`, `agents/skills/formato-csv-istat/SKILL.md`, `agents/stato/sfida-requisiti-architetto.md`, `agents/stato/sfida-requisiti-conformita.md`.
> Tutti gli importi dell'app sono **annuali**. I dati sono **dimostrativi e inventati**.

---

## 1. Persona e problema

Marco ha 34 anni, vive da solo in affitto a Milano (Nord-ovest) e ha una bassa alfabetizzazione finanziaria.
Sa più o meno quanto paga ogni anno per casa, auto, bollette e spesa, ma non ha un termine di paragone.
Non sa se le sue spese sono "normali" per una persona nella sua situazione e non capisce dove vanno i suoi soldi.
Le statistiche ufficiali sono tabelle lunghe, con parole tecniche, e non sono riferite al suo profilo.
Si blocca perché non riesce a mettere le sue cifre accanto a quelle di persone come lui.

## 2. Obiettivo educativo

Alla fine Marco capisce:
1. quanto spende in un anno, in totale e per ciascuna delle 5 voci (Casa, Sport e tempo libero, Auto e mobilità, Utenze, Spesa);
2. di quanto la sua spesa è sopra, sotto o in linea con la media di persone con un profilo come il suo, in euro e in percentuale;
3. cosa include ogni voce di spesa e alcuni concetti di base (spesa fissa, spesa variabile, cosa fa cambiare una voce);
4. con chi viene confrontato e che la media è un riferimento, non un obiettivo.

## 3. User story

- **US-01** – Come Marco, voglio accedere con nome utente e password, per ritrovare i miei dati.
- **US-02** – Come Marco, voglio confermare l'accesso con una notifica, per vedere come funziona un accesso protetto (simulato nella demo).
- **US-03** – Come Marco, voglio rispondere a poche domande su di me, per essere confrontato con persone con un profilo come il mio.
- **US-04** – Come Marco, voglio scrivere quanto spendo in un anno per ciascuna delle 5 voci, per confrontare le mie spese.
- **US-05** – Come Marco, voglio vedere in un grafico a ragnatela la mia spesa e la spesa campione per ogni voce, per capire a colpo d'occhio dove le differenze sono più grandi.
- **US-06** – Come Marco, voglio leggere per ogni voce le cifre e una spiegazione semplice, per capire cosa include la voce e cosa significa la differenza.
- **US-07** – Come Marco, voglio sapere con chi vengo confrontato e se il confronto è approssimato, per capire quanto vale il risultato.
- **US-08** – Come Marco, voglio ritrovare profilo e spese al prossimo accesso, per non doverli scrivere di nuovo.
- **US-09** – Come amministratore della demo, voglio caricare il file dati, per aggiornare domande, punteggi e spese campione.
- **US-10** – Come amministratore della demo, voglio vedere quali righe del file sono state scartate e perché, per correggere il file.

## 4. Schermate

### Elemento comune: intestazione e menu hamburger
- **Dove compare**: in cima a S-03, S-04 e S-05. **Non** compare in S-01 e S-02.
- **Contenuto**: nome dell'app "ContiInTasca" e pulsante menu (icona hamburger con etichetta accessibile "Menu").
- **Voci del menu**, in questo ordine: Profilo (S-04), Analisi di spesa (S-03), Amministrazione (S-05), Esci (torna a S-01 e chiude l'accesso; vedi DA-01).
- **Azioni**: apre e chiude con clic, tocco, Invio o Spazio; si chiude con Esc o scegliendo una voce. La voce della pagina corrente è evidenziata.

### S-01 – Login
- **Scopo**: entrare nell'app (accesso dimostrativo).
- **Testi fissi**: nome dell'app; una frase sullo scopo ("Confronta le tue spese di un anno con quelle di persone con un profilo come il tuo."); avviso ben visibile: "Accesso dimostrativo: puoi usare qualsiasi nome utente e password."
- **Campi**:
  | Nome | Tipo | Obbligatorio | Valori ammessi |
  |---|---|---|---|
  | Nome utente | testo | sì | da 1 a 50 caratteri, dopo aver tolto gli spazi all'inizio e alla fine |
  | Password | testo nascosto | sì | da 1 a 100 caratteri |
- **Azioni**: "Accedi" → S-02 (se i campi sono validi).
- **Messaggi di errore**:
  - Campo vuoto: dice quale campo manca, che serve per entrare, e che si può scrivere qualsiasi valore.
  - Campo troppo lungo: dice quale campo supera il limite, il numero massimo di caratteri, e invita a correggerlo.

### S-02 – Conferma accesso (MFA simulata)
- **Scopo**: mostrare il secondo passaggio di accesso.
- **Testi fissi**: "Ti abbiamo inviato una notifica sul telefono." e "Accesso dimostrativo: la notifica è simulata e viene accettata da sola." Conto alla rovescia visibile dei secondi mancanti (5, 4, 3, 2, 1).
- **Campi**: nessuno.
- **Azioni**: "Annulla" → torna a S-01 senza accesso. Allo scadere dei 5 secondi l'accesso è confermato e si va alla pagina indicata da RB-03.
- **Messaggi di errore**: nessuno.

### S-03 – Analisi di spesa (pagina principale)
- **Scopo**: mostrare il confronto tra la spesa di Marco e la spesa campione, voce per voce e in totale.
- **Disposizione**: due sezioni affiancate: **sinistra** ragnatela, **destra** box testuale. Su schermi stretti il box va sotto la ragnatela.
- **Ragnatela (sinistra)**: vedi RB-15. Legenda con "La tua spesa" e "Spesa campione".
- **Box (destra)**, tre sezioni in quest'ordine:
  1. **Spesa corrente**: importo della voce mostrata, oppure totale delle 5 voci se la voce mostrata è il totale;
  2. **Spesa campione**: importo campione della voce mostrata, oppure totale campione delle 5 voci;
  3. **Da sapere**: elenco puntato di 3 frasi (RB-17).
  Il titolo del box è il nome della voce mostrata, oppure "Totale". Un controllo "Vedi i totali" riporta il box al totale (RB-16).
- **Sotto le due sezioni, sempre visibili** (senza aprire nulla):
  - "Con chi ti confronti" (RB-18), con l'avviso di profilo approssimato quando serve (RB-10);
  - "Parole utili" con 3 definizioni (RB-19);
  - frase "La media è un riferimento, non un obiettivo: ogni situazione è diversa.";
  - disclaimer e fonte (sezione 7).
- **Campi**: nessuno.
- **Azioni**: passaggio del mouse, focus da tastiera, clic, tocco, Invio o Spazio su un ramo (RB-16); Esc; "Vedi i totali".
- **Messaggi**: se il profilo non è completo si va a S-04 con l'avviso indicato da RB-24; se non ci sono dati caricati vale RB-25.

### S-04 – Profilo
- **Scopo**: raccogliere le risposte di Marco e le sue 5 spese annuali.
- **Parte A – "Le tue informazioni"**: un campo per ogni domanda caricata dal file dati, nell'ordine del file. Ogni domanda del file ha un'**etichetta breve** (usata in RB-18) e un **testo della domanda** (mostrato qui); sotto il testo compare la breve spiegazione, se presente nel file.
  | Nome | Tipo | Obbligatorio | Valori ammessi |
  |---|---|---|---|
  | Domanda di tipo "scelta" (es. Provincia, Dove vivi, Sesso, Fai sport) | scelta singola tra le opzioni del file | sì | solo le opzioni caricate |
  | Domanda di tipo "numero" (es. Età, Reddito lordo annuo, Numero di autovetture, Numero di ciclomotori, Membri del nucleo familiare) | numero intero | sì | da minimo a massimo delle fasce caricate per quella domanda (RB-07) |
- **Parte B – "Quanto spendi in un anno"**: sotto ogni campo compare la frase "cosa include" della voce (RB-17, tabella A) e l'aiuto "Scrivi l'importo di un anno intero, solo cifre. Per esempio 12000."
  | Nome | Tipo | Obbligatorio | Valori ammessi |
  |---|---|---|---|
  | Casa | importo annuo in euro | sì | intero da 0 a 999999, solo cifre |
  | Sport e tempo libero | importo annuo in euro | sì | come sopra |
  | Auto e mobilità | importo annuo in euro | sì | come sopra |
  | Utenze | importo annuo in euro | sì | come sopra |
  | Spesa | importo annuo in euro | sì | come sopra |
- **Precompilazione**: se il nome utente ha già un profilo salvato, i campi mostrano i valori salvati ancora validi (RB-24).
- **Azioni**: "Salva e vedi l'analisi" → controlla tutti i campi; se sono tutti validi salva e va a S-03; altrimenti non salva nulla.
- **Messaggi di errore** (vicino al campo, in tre parti: cosa è successo / perché / cosa puoi fare), più un riepilogo in alto "Ci sono N campi da correggere.":
  - Risposta mancante: manca la risposta; serve per trovare persone con un profilo come il tuo; scegli una risposta o scrivi un numero.
  - Numero fuori intervallo o non intero (domande): il valore non è valido; per questa domanda si accettano numeri interi da X a Y; correggi il campo.
  - Importo negativo (es. "-100"): l'importo non è valido; una spesa non può essere sotto zero; scrivi un numero da 0 a 999999.
  - Importo con caratteri diversi dalle cifre (es. "12.000", "12,5", "300 €", "abc"): l'importo non è valido; puoi usare solo cifre, senza punti, virgole o simbolo €; scrivi per esempio 12000.
  - Importo oltre 999999: l'importo supera il limite; l'app accetta importi fino a 999999 €; correggi il campo.
- **Nessun dato caricato**: vale RB-25.

### S-05 – Amministrazione
- **Scopo**: caricare il file dati dimostrativo che popola domande, punteggi e spese campione.
- **Testi fissi**: "I dati di questa demo sono inventati, ispirati all'Indagine ISTAT sulle spese delle famiglie."
- **Riquadro "Dati attuali"**: data e ora dell'ultimo caricamento riuscito, nome del file, numero di domande e numero di fasce di punteggio caricate; oppure "Nessun dato caricato.".
- **Campi**:
  | Nome | Tipo | Obbligatorio | Valori ammessi |
  |---|---|---|---|
  | File dati | file | sì | file con estensione .csv, massimo 2 MB, formato definito in `agents/skills/formato-csv-istat/SKILL.md` |
- **Azioni**: "Carica" → avvia il caricamento e mostra l'esito.
- **Esito** (RB-20, RB-21, RB-22):
  - riuscito: "Caricamento completato." con righe lette, righe importate, righe scartate; tabella delle righe scartate con **numero di riga** e **motivo in parole semplici**;
  - rifiutato: messaggio in tre parti; i dati precedenti restano invariati.
- **Messaggi di errore**:
  - Nessun file scelto: nessun file scelto; serve un file da caricare; scegli un file .csv e premi Carica.
  - File non CSV, vuoto, oltre 2 MB, con intestazione sbagliata o senza dati validi: il file non è stato caricato; motivo specifico; cosa correggere nel file.

## 5. Regole di business

**Accesso e navigazione**
- **RB-01 – Accesso simulato**: è accettata qualsiasi coppia nome utente / password che rispetta i limiti di S-01. Il nome utente si confronta senza distinguere maiuscole e minuscole e senza gli spazi all'inizio e alla fine ("Marco" = " marco "). La password non viene mai conservata.
- **RB-02 – MFA simulata**: S-02 resta visibile 5 secondi (tolleranza ±0,5 s), poi l'accesso è confermato senza azioni dell'utente. "Annulla" interrompe l'attesa e riporta a S-01 senza accesso.
- **RB-03 – Prima pagina dopo l'accesso**: se il nome utente ha un profilo completo (RB-05) si va a S-03; altrimenti si va a S-04 (con l'avviso indicato da RB-24).
- **RB-04 – Pagine protette e durata dell'accesso**: S-03, S-04 e S-05 si aprono solo dopo login e MFA. Chi apre una di queste pagine senza accesso viene portato a S-01. S-05 è aperta a chiunque abbia fatto l'accesso. L'accesso vale **per la scheda del browser in cui è stato fatto**: resta valido se si ricarica la pagina o si scrive a mano l'indirizzo di S-03, S-04 o S-05 nella stessa scheda; si perde con "Esci" (che porta a S-01) o chiudendo la scheda. Una scheda nuova richiede un nuovo accesso.

**Profilo**
- **RB-05 – Profilo completo**: ha una risposta valida a **ogni** domanda caricata e un importo valido per **tutte e 5** le voci.
- **RB-06 – Importi di Marco**: annuali, interi da 0 a 999999 €, scritti solo con cifre (0-9). Gli spazi all'inizio e alla fine si ignorano. Gli zeri iniziali si ignorano ("0120" = 120). Un valore che inizia con "-" seguito da cifre ha il messaggio "importo negativo".
- **RB-07 – Risposte numeriche**: numeri interi. Valori ammessi: dal minimo più basso al massimo più alto tra le fasce di risposta caricate per quella domanda, estremi inclusi. Un valore che cade in un vuoto tra due fasce usa la fascia con l'estremo più vicino; a parità, la fascia più bassa.
- **RB-08 – Punteggio di una risposta**: per una domanda a scelta è il punteggio dell'opzione scelta; per una domanda numerica è il punteggio della fascia che contiene il valore (minimo ≤ valore ≤ massimo).

**Calcolo del confronto**
- **RB-09 – Punteggio del profilo e fascia**: il punteggio del profilo è la somma dei punteggi di tutte le risposte. Si usa la fascia di punteggio con minimo ≤ punteggio ≤ massimo.
- **RB-10 – Fascia più vicina (profilo approssimato)**: se il punteggio non cade in nessuna fascia, si usa la fascia con la distanza minima, dove distanza = differenza tra il punteggio e l'estremo più vicino della fascia; a parità, la fascia con il minimo più basso. S-03 mostra allora l'avviso: "Nei dati non c'è un gruppo con il tuo punteggio (Z). Ti confrontiamo con il gruppo più vicino: punteggio da X a Y."
- **RB-11 – Spesa campione**: per ogni voce è l'importo della fascia usata, arrotondato all'euro (da ,50 in su per eccesso) **prima** di ogni calcolo. Il totale campione è la somma dei 5 importi arrotondati. Il totale corrente è la somma dei 5 importi di Marco. Il calcolo si fa ogni volta che si apre S-03, con i dati caricati in quel momento.
- **RB-12 – Differenza e percentuale**: differenza = spesa corrente − spesa campione (in euro). Percentuale = |differenza| ÷ spesa campione × 100, arrotondata all'intero (da ,5 in su per eccesso). Se la spesa campione è 0 €, la percentuale non si calcola e non si mostra.
- **RB-13 – Esito**: si confronta |differenza| con il 10% della spesa campione, senza arrotondare.
  - **IN_LINEA**: |differenza| ≤ 10% della spesa campione (estremi inclusi). Se differenza = 0 € l'esito è sempre IN_LINEA, anche con campione 0 €.
  - **SOPRA**: differenza > 10% della spesa campione; con campione 0 €, qualsiasi spesa corrente > 0 €.
  - **SOTTO**: differenza < −10% della spesa campione.
  - Se l'esito è SOPRA o SOTTO e la percentuale arrotondata vale 10%, si usano le righe "P = 10" della tabella B ("oltre il 10%").
  - Esempi: campione 5.000 € → 5.500 € IN_LINEA; 5.501 € SOPRA; 4.500 € IN_LINEA; 4.499 € SOTTO.
- **RB-14 – Formato dei numeri**: importi interi con il punto delle migliaia e il simbolo dopo la cifra (`12.000 €`, `0 €`); percentuali intere (`24%`). Gli importi nel box e nella ragnatela sono sempre in euro all'anno.

**Ragnatela e box**
- **RB-15 – Ragnatela**: 5 rami in quest'ordine fisso: Casa, Sport e tempo libero, Auto e mobilità, Utenze, Spesa. Due forme sovrapposte e distinguibili anche senza colore (es. linea continua / tratteggiata): "La tua spesa" e "Spesa campione". Accanto al nome di ogni ramo compaiono i due importi. Tutti i rami usano la **stessa scala**, da 0 € al valore più alto tra i 10 importi mostrati.
- **RB-16 – Navigazione (un solo stato: "voce mostrata")**: ogni ramo si raggiunge con Tab / Maiusc+Tab nell'ordine di RB-15, con focus visibile. Esiste un solo stato, la **voce mostrata**, che all'apertura di S-03 è il totale. La voce mostrata diventa quella del ramo che riceve passaggio del mouse, focus da tastiera, clic, tocco, Invio o Spazio, e **resta** tale finché un altro ramo non la cambia (anche se il mouse o il focus escono dalla ragnatela). Esc o "Vedi i totali" riportano la voce mostrata al totale. Un nuovo clic sul ramo già mostrato non cambia nulla. Il box mostra la voce mostrata; il ramo corrispondente è evidenziato nella ragnatela.
- **RB-17 – Contenuto di "Da sapere"**: sempre 3 frasi, in quest'ordine:
  1. la frase "cosa include" della voce (tabella A);
  2. la frase con la cifra, costruita con la differenza e la percentuale (tabella B);
  3. la frase-concetto per voce ed esito (tabella C).
  Questi testi sono fissi nell'app, non arrivano dal file dati. Ogni frase rispetta `linguaggio-semplice` (max 20 parole, nessuna espressione vietata).

  **Tabella A – Cosa include**
  | Voce | Frase |
  |---|---|
  | Casa | Questa voce include affitto o rata del mutuo, spese di condominio e piccole riparazioni. |
  | Sport e tempo libero | Questa voce include palestra, attrezzatura sportiva, cinema, concerti, ristoranti, viaggi e uscite. |
  | Auto e mobilità | Questa voce include carburante, assicurazione, bollo e manutenzione. |
  | Utenze | Questa voce include luce, gas, acqua, rifiuti, internet e telefono. |
  | Spesa | Questa voce include cibo, bevande e prodotti per la casa e per l'igiene. |
  | Totale | Il totale è la somma delle cinque voci: Casa, Sport e tempo libero, Auto e mobilità, Utenze, Spesa. |

  **Tabella B – Frase con la cifra** (`<Voce>` = nome della voce, oppure "In totale"; D = |differenza|; P = percentuale arrotondata)
  | Esito | Testo modello |
  |---|---|
  | SOPRA, P > 10 | Per <Voce> spendi D € all'anno in più della media di persone come te. È il P% in più. |
  | SOPRA, P = 10 | Per <Voce> spendi D € all'anno in più della media di persone come te. È oltre il 10% in più. |
  | SOTTO, P > 10 | Per <Voce> spendi D € all'anno in meno della media di persone come te. È il P% in meno. |
  | SOTTO, P = 10 | Per <Voce> spendi D € all'anno in meno della media di persone come te. È oltre il 10% in meno. |
  | IN_LINEA, D > 0 | Per <Voce> la tua spesa è in linea con la media di persone come te. La differenza è di D € all'anno in più / in meno. |
  | IN_LINEA, D = 0 | Per <Voce> la tua spesa è uguale alla media di persone come te. |
  | SOPRA con campione 0 € | Per <Voce> spendi D € all'anno. La spesa campione per questa voce è 0 €. |
  Per il totale si scrive "In totale spendi…" / "In totale la tua spesa è…".

  **Tabella C – Frase-concetto per voce ed esito**
  | Voce | SOPRA | IN_LINEA | SOTTO |
  |---|---|---|---|
  | Casa | Una differenza in questa voce può dipendere dalla città e dalla grandezza della casa. | L'affitto è una spesa fissa: si ripete ogni mese con lo stesso importo. | Chi vive in una casa di proprietà senza mutuo paga in questa voce solo condominio e riparazioni. |
  | Sport e tempo libero | Questa è una spesa variabile: l'importo cambia da mese a mese in base alle attività. | Alcune spese di questa voce sono fisse, come un abbonamento; altre cambiano ogni mese. | In questa voce le abitudini personali contano molto, anche tra persone con lo stesso profilo. |
  | Auto e mobilità | Una parte di queste spese è fissa (bollo, assicurazione): si ripete ogni anno con lo stesso importo. | Il carburante è una spesa variabile: cambia con i chilometri percorsi e con il prezzo alla pompa. | Questa voce cambia molto con il numero di auto e ciclomotori e con i chilometri percorsi. |
  | Utenze | Le bollette di luce e gas cambiano con le stagioni e con il prezzo dell'energia. | Una bolletta ha una parte fissa, uguale ogni mese, e una parte che cambia con i consumi. | Questa voce cambia con il numero di persone in casa, la grandezza della casa e il riscaldamento. |
  | Spesa | Questa voce cambia con il numero di pasti fatti in casa e con i prezzi della zona. | La spesa per il cibo è variabile: l'importo cambia da settimana a settimana. | Chi mangia spesso fuori casa ha una parte del cibo nella voce Sport e tempo libero. |
  | Totale | Una differenza nel totale può venire da una sola voce o da più voci insieme. | In linea con la media significa che la differenza è entro il 10% della spesa campione. | Una differenza nel totale può venire da una sola voce o da più voci insieme. |

- **RB-18 – Con chi ti confronti**: S-03 mostra: "Ti confronti con persone con un profilo come il tuo: punteggio del profilo da X a Y. Il tuo punteggio è Z." Seguito dall'elenco delle risposte di Marco, una per riga, nell'ordine delle domande, nel formato "<etichetta breve>: <risposta>". Le risposte a scelta si scrivono con il testo dell'opzione del file; le risposte numeriche con il punto delle migliaia e senza unità di misura (es. "Provincia: Milano", "Età: 34", "Reddito lordo annuo: 28.000"). X–Y è la fascia usata (RB-09 o RB-10).
- **RB-19 – Parole utili** (testi fissi, max 25 parole ciascuno):
  - **Spesa campione**: quanto spendono in media in un anno, secondo i dati dimostrativi, persone con un profilo come il tuo.
  - **Punteggio del profilo**: un numero calcolato dalle tue risposte. Serve a trovare nei dati il gruppo di persone più simile a te.
  - **In linea con la media**: la tua spesa è diversa dalla spesa campione di non più del 10%.

**File dati (Amministrazione)** – il formato di dettaglio e i codici di scarto li definisce l'architetto in `formato-csv-istat`.
- **RB-20 – Errori sull'intero file**: file non .csv, vuoto, oltre 2 MB, codifica o intestazione non valida → caricamento **rifiutato**, nessun dato cambiato, messaggio in tre parti.
- **RB-21 – Errori su singole righe**: una riga non valida viene **scartata** e le altre continuano. Ogni scarto è mostrato con numero di riga e motivo in parole semplici. Sono righe scartate almeno: campo obbligatorio mancante; numero non valido; importo o punteggio negativo; voce di spesa diversa dalle 5 previste (confronto senza distinguere maiuscole e minuscole); riferimento a una domanda o a una fascia inesistente; fascia che si sovrappone a una già letta per la stessa domanda o tra le fasce di punteggio. Una fascia di punteggio senza importo per tutte e 5 le voci non viene usata e le sue righe risultano scartate con questo motivo.
- **RB-22 – Sostituzione dei dati**: un caricamento riuscito sostituisce **tutti** i dati precedenti (o tutto, o niente). Il caricamento è rifiutato, come in RB-20, se dopo gli scarti non resta almeno 1 domanda con almeno 1 opzione o fascia con punteggio e almeno 1 fascia di punteggio completa delle 5 voci.
- **RB-23 – File di esempio**: il file dimostrativo contiene le 9 domande dell'idea, ognuna con etichetta breve e testo della domanda: Provincia, Età, Dove vivi [Appartamento di proprietà / Affitto], Sesso [M / F / Preferisco non rispondere], Reddito lordo annuo, Fai sport [Sì / No], Numero di autovetture, Numero di ciclomotori, Membri del nucleo familiare. Spiegazioni obbligatorie nel file:
  - "Reddito lordo annuo": "Quello che guadagni in un anno, prima di tasse e contributi.";
  - "Sesso": "Serve solo a trovare il tuo gruppo nei dati. Non è un giudizio su di te."
  L'opzione "Preferisco non rispondere" ha punteggio 0 ed è una risposta valida per il profilo completo (RB-05).
  Contiene le 5 voci. **Profilo di Marco** (9 risposte, nell'ordine): Provincia Milano, Età 34, Dove vivi Affitto, Sesso M, Reddito lordo annuo 28000, Fai sport Sì, Numero di autovetture 1, Numero di ciclomotori 0, Membri del nucleo familiare 1. Questo profilo cade in una fascia con Casa 12.000 €, Sport e tempo libero 3.000 €, Auto e mobilità 5.000 €, Utenze 5.000 €, Spesa 8.000 €.
  **Profilo fuori fascia** (per CA-09): Provincia Milano, Età 70, Dove vivi Appartamento di proprietà, Sesso F, Reddito lordo annuo 150000, Fai sport No, Numero di autovetture 3, Numero di ciclomotori 2, Membri del nucleo familiare 6. Tutti i valori sono ammessi dal file e il punteggio non cade in nessuna fascia.
  L'architetto riporta in `formato-csv-istat` il punteggio atteso Z di entrambi i profili e la fascia attesa X–Y (per il profilo fuori fascia, quella scelta da RB-10). Serve anche un secondo file di esempio con almeno 3 righe errate di tipo diverso.

**Dati conservati**
- **RB-24 – Cosa si conserva e avvisi sul profilo**: per ogni nome utente: risposte alle domande, 5 importi, data dell'ultimo salvataggio. Per i dati caricati: domande (etichetta breve, testo, spiegazione), opzioni e fasce di risposta con punteggio, fasce di punteggio con importi per voce, nome del file, data e ora del caricamento. La spesa campione non si conserva: si calcola (RB-11). L'avviso in S-04 dipende **solo dallo stato del profilo**, non dal momento:
  - **profilo salvato ma non più completo** (dopo un nuovo caricamento: domanda nuova, opzione o valore non più ammesso): ogni volta che S-04 si apre (dopo l'accesso, dal menu o perché si è aperto S-03) mostra l'avviso "Le domande del profilo sono cambiate. Controlla le tue risposte." con i campi ancora validi già compilati;
  - **nessun profilo salvato**: dopo l'accesso o dal menu S-04 si apre vuota senza avviso; se si apre S-03, si va a S-04 con l'avviso "Per vedere l'analisi di spesa compila prima il tuo profilo.".
- **RB-25 – Nessun dato caricato**: S-04 e S-03 mostrano "Non ci sono ancora dati per il confronto. Puoi caricarli dalla pagina Amministrazione." con un collegamento a S-05; il questionario non compare.
- **RB-26 – Dove verificare** _(aggiunta dopo l'approvazione, su decisione dell'utente)_: nel box di S-03, **solo** quando la voce mostrata ha esito "sopra la media" (comprese le frasi "oltre il 10%"), sotto "Da sapere" compare il riquadro **"Dove verificare"** con i rimandi a fonti istituzionali, testo esatto:
  | Voce | Frase | Collegamento |
  |---|---|---|
  | Auto e mobilità | "Puoi vedere i prezzi dei carburanti dei distributori vicino a te sull'Osservatorio prezzi carburanti del MIMIT." | https://carburanti.mise.gov.it |
  | Auto e mobilità | "Puoi confrontare il prezzo dell'assicurazione auto obbligatoria (RC auto) sul preventivatore pubblico IVASS." | https://www.preventivass.it |
  | Utenze | "Puoi confrontare le offerte di luce e gas sul Portale Offerte di ARERA, un servizio pubblico e gratuito." | https://www.ilportaleofferte.it |
  | Casa, Sport e tempo libero, Spesa | "Puoi trovare spiegazioni semplici sul bilancio familiare nel portale «L'economia per tutti» della Banca d'Italia." | https://economiapertutti.bancaditalia.it |
  In fondo al riquadro, sempre: "Sono servizi pubblici e gratuiti. La scelta resta tua." Il nome della fonte è il testo del collegamento; il collegamento si apre in una nuova scheda (`target="_blank"`, `rel="noopener noreferrer"`) e lo dichiara a parole ("si apre in una nuova scheda"). Per il totale e per gli esiti "in linea" e "sotto la media" il riquadro non compare. Il riquadro non contiene mai indicazioni su cosa scegliere.

## 6. Criteri di accettazione

- **CA-01** (US-01) – **Dato** che sono su S-01, **quando** scrivo nome utente "marco" e una password qualsiasi e premo "Accedi", **allora** vedo S-02. **Quando** invece lascio vuoto il nome utente, **allora** resto su S-01 e vedo il messaggio di campo mancante. S-01 mostra sempre l'avviso "Accesso dimostrativo: puoi usare qualsiasi nome utente e password."
- **CA-02** (US-02, US-08) – **Dato** che sono su S-02, **quando** passano 5 secondi senza che io faccia nulla, **allora** con un nome utente nuovo vedo S-04 vuota; con un nome utente che ha già un profilo completo (scritto anche con maiuscole diverse) vedo S-03, e aprendo S-04 dal menu trovo le risposte e gli importi salvati.
- **CA-03** (US-01) – **Dato** che non ho fatto l'accesso, **quando** apro direttamente l'indirizzo di S-03, S-04 o S-05, **allora** vedo S-01. **Dato** che ho fatto l'accesso, **quando** ricarico la pagina nella stessa scheda, **allora** resto sulla stessa pagina senza nuovo accesso. **Quando** scelgo "Esci" dal menu, **allora** vedo S-01 e non posso riaprire S-03 senza un nuovo accesso.
- **CA-04** (US-03, US-04) – **Dato** che sono su S-04 con tutte le domande compilate, **quando** scrivo "-100" in Casa, "12.000" in Utenze, lascio vuoto Spesa e premo "Salva e vedi l'analisi", **allora** vedo i tre messaggi di RB-06/S-04 vicino ai campi, il riepilogo "Ci sono 3 campi da correggere." e nulla viene salvato. **Quando** correggo con 12000, 5000 e 8000 e salvo, **allora** vedo S-03 con i nuovi importi.
- **CA-05** (US-05) – **Dato** un profilo completo, **quando** apro S-03, **allora** vedo una ragnatela con 5 rami nell'ordine di RB-15, due forme con legenda "La tua spesa" e "Spesa campione", i due importi in euro accanto a ogni ramo e la stessa scala su tutti i rami.
- **CA-06** (US-06) – **Dato** Auto e mobilità con spesa corrente 6.200 € e spesa campione 5.000 €, **quando** porto il mouse sul ramo, **oppure** ci arrivo con Tab, **oppure** lo clicco, **allora** il box mostra il titolo "Auto e mobilità", "Spesa corrente: 6.200 €", "Spesa campione: 5.000 €" e in "Da sapere" le 3 frasi: tabella A, "Per Auto e mobilità spendi 1.200 € all'anno in più della media di persone come te. È il 24% in più.", tabella C (SOPRA).
- **CA-07** (US-06) – **Dato** che sono su S-03, **quando** la voce mostrata è il totale (all'apertura, dopo Esc o dopo "Vedi i totali"), **allora** il box mostra il titolo "Totale", la somma delle 5 spese correnti, la somma delle 5 spese campione e le 3 frasi "Da sapere" della riga Totale con l'esito calcolato sui totali.
- **CA-08** (US-06) – **Dato** la voce Utenze con spesa campione 5.000 €, **quando** la spesa corrente è 5.500 €, 5.501 €, 4.500 €, 4.499 € o 5.000 €, **allora** l'esito mostrato è rispettivamente IN_LINEA, SOPRA, IN_LINEA, SOTTO, IN_LINEA, con la frase di tabella B e C corrispondente. In particolare la frase di tabella B è, per 5.501 €: "Per Utenze spendi 501 € all'anno in più della media di persone come te. È oltre il 10% in più."; per 4.499 €: "Per Utenze spendi 501 € all'anno in meno della media di persone come te. È oltre il 10% in meno."; per 5.000 €: "Per Utenze la tua spesa è uguale alla media di persone come te."
- **CA-09** (US-07) – **Dato** il file di esempio caricato e il "profilo fuori fascia" di RB-23, **quando** apro S-03, **allora** il confronto usa la fascia più vicina secondo RB-10 e vedo l'avviso "Nei dati non c'è un gruppo con il tuo punteggio (Z). Ti confrontiamo con il gruppo più vicino: punteggio da X a Y." con Z, X e Y uguali ai valori attesi riportati in `formato-csv-istat`.
- **CA-10** (US-07) – **Dato** che sono su S-03, **quando** guardo la pagina senza aprire nulla, **allora** vedo "Con chi ti confronti" con il mio punteggio e l'elenco delle mie risposte nel formato di RB-18, le 3 "Parole utili", la frase "La media è un riferimento, non un obiettivo: ogni situazione è diversa.", il disclaimer e la fonte esatti (sezione 7); e nessun testo della pagina contiene le espressioni vietate di `linguaggio-semplice` §3 né le parole "troppo", "poco", "bene", "male".
- **CA-11** (US-09) – **Dato** che sono su S-05, **quando** carico il file di esempio valido, **allora** vedo "Caricamento completato." con righe lette, importate e scartate (0); "Dati attuali" mostra il nome del file, data e ora, il numero di domande (9) e di fasce; S-04 mostra le 9 domande.
- **CA-12** (US-10) – **Dato** il secondo file di esempio con almeno 3 righe errate, **quando** lo carico, **allora** vedo per ogni riga errata il numero di riga e il motivo in parole semplici, e le righe valide risultano importate e usate da S-04 e S-03.
- **CA-13** (US-09) – **Dato** che ci sono dati caricati, **quando** carico un file .txt, un file .csv vuoto, un file oltre 2 MB o un file con intestazione sbagliata, **allora** vedo un messaggio in tre parti che dice che il file non è stato caricato e perché, e "Dati attuali" resta uguale a prima.
- **CA-14** (US-03) – **Dato** che non è mai stato caricato nessun file, **quando** apro S-04 o S-03, **allora** vedo "Non ci sono ancora dati per il confronto. Puoi caricarli dalla pagina Amministrazione." con un collegamento a S-05, e il questionario non compare.

## 7. Vincoli di conformità

- L'app **mostra fatti e spiega concetti**; non dice mai cosa fare con i soldi. Non ci sono budget consigliati, obiettivi di risparmio, indicazioni su dove tagliare, confronti tra prodotti, banche o fornitori.
- Vietate le espressioni di `agents/skills/linguaggio-semplice/SKILL.md` §3, in ogni forma, e i giudizi "troppo", "poco", "bene", "male", "eccessivo", "virtuoso". Confronti solo con "più della media", "meno della media", "in linea con la media".
- **Disclaimer**, visibile in S-03 senza aprire nulla, testo esatto: "Questo confronto ha solo scopo informativo ed educativo. Non è un consiglio finanziario."
- **Fonte**, sotto il confronto in S-03, testo esatto: "Dati dimostrativi ispirati all'Indagine ISTAT sulle spese delle famiglie, anno 2024." L'anno indica il riferimento dell'ispirazione, non dati reali (decisione dell'utente su DA-02). I dati non vengono mai presentati come dati ISTAT reali, né nell'app né nel README.
- Frase obbligatoria in S-03: "La media è un riferimento, non un obiettivo: ogni situazione è diversa."
- Si dice sempre **con chi** si confronta (RB-18) e se il confronto è **approssimato** (RB-10).
- La domanda "Sesso" serve solo a trovare il gruppo statistico: ha la spiegazione di RB-23 e l'opzione "Preferisco non rispondere". Nessun testo dell'app collega il sesso a una voce di spesa.
- Il segno della differenza non cambia mai con l'arrotondamento (RB-11, RB-12, RB-13).
- Tutti i testi visibili seguono `linguaggio-semplice` §1 (max 20 parole per frase, "tu", voce attiva, errori in tre parti).
- Il login e la MFA sono dichiarati come dimostrativi in S-01 e S-02.

## 8. Fuori ambito

- Login reale, MFA reale, registrazione, recupero password, scadenza dell'accesso a tempo.
- Ruoli e permessi: l'Amministrazione è aperta a chiunque abbia fatto l'accesso.
- Modifica dei dati caricati dall'interfaccia (si può solo caricare un nuovo file).
- Importi mensili o conversioni mensile/annuale; voci di spesa diverse dalle 5 previste.
- Storico dei confronti nel tempo, esportazione, stampa, condivisione.
- Cancellazione del profilo salvato.
- Collegamento a conti bancari o importazione di movimenti.
- Qualsiasi suggerimento su cosa fare con i soldi (budget, risparmio, tagli, prodotti).
- Dati ISTAT reali; lingue diverse dall'italiano.

## 9. Domande aperte

- **DA-01** – Voce "Esci" nel menu: non era nell'idea, ma serve per provare il cambio di nome utente nella demo (CA-03). Ipotesi: inclusa. **RISOLTO dall'utente: inclusa.**
- **DA-02** – Anno nella fonte: **RISOLTO dall'utente**, si cita "anno 2024".

## 10. Modifiche dopo la sfida

- **OB-A-01** → RB-23 (9 risposte del profilo di Marco e "profilo fuori fascia" esplicito; Z e X–Y attesi riportati dall'architetto in `formato-csv-istat`), CA-09 (usa il profilo fuori fascia e i valori attesi).
- **OB-A-02** → RB-16 (un solo stato "voce mostrata"; tolto l'annullamento con nuovo clic), S-03 Box e Azioni, CA-07 (formulazione con "voce mostrata").
- **OB-A-03** → Tabella B di RB-17 (righe "SOPRA, P = 10" e "SOTTO, P = 10" con testo esatto), RB-13, CA-08 (frasi esatte su Utenze).
- **OB-A-04** → RB-24 (avviso deciso dallo stato del profilo, valido dopo l'accesso, dal menu e da S-03), RB-03, S-03 Messaggi.
- **OB-A-05** → RB-04 (accesso valido per la scheda: resta con il ricaricamento, si perde con "Esci" o chiudendo la scheda), CA-03 (caso ricaricamento), Fuori ambito (scadenza "a tempo").
- **OB-A-06** → S-04 Parte A (etichetta breve e testo della domanda), RB-18 (formato "<etichetta>: <risposta>", numeri con punto delle migliaia senza unità), RB-24 (dati conservati), RB-23, CA-10.
- **OB-C-01** → RB-23 (spiegazione obbligatoria per "Sesso" e opzione "Preferisco non rispondere" con punteggio 0), sezione 7 (nuovo vincolo sulla domanda "Sesso"). La domanda resta, perché chiesta dall'utente.
