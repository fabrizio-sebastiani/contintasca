# Idea dell'utente (parole dell'utente, raccolte dall'orchestratore)

## Nome
L'applicazione si chiama **"In tasca mia"**. _(Rinominata dall'utente in **"ContiInTasca"**.)_

## Accesso
- Pagina di **login con username e password**. Nella demo è **simulata**: accetta qualsiasi valore.
- Accesso con **MFA simulata**: dopo il login l'app mostra che è stata inviata una notifica, **attende 5 secondi** e poi la considera accettata.

## Pagina di amministrazione
- Oltre al login c'è una **pagina di amministrazione** da cui si **caricano i dati ISTAT**.
- Si raggiunge da un **menu hamburger in cima alla pagina**.
- Il file da caricare **non viene fornito dall'utente**: trattandosi di una demo, va **inventato** (dati fittizi ma plausibili, dichiarati come tali).
- Il caricamento popola **almeno tre tabelle**:
  1. **Domande che definiscono il profilo**. Quelle indicate dall'utente, da mostrare nella demo:
     - Dove risiedi (provincia)
     - Età
     - Dove vivi [appartamento di proprietà, affitto]
     - Sesso [M/F]
     - Reddito lordo annuo
     - Fai sport
     - Numero di autovetture
     - Numero di ciclomotori
     - Membri del nucleo familiare
  2. **Score**: a ogni domanda è associato uno score.
  3. **Dimensioni di spesa per score**: a ogni score sono associate delle dimensioni di spesa con un importo. Esempi indicati dall'utente, da mostrare nella demo:
     - Casa: 12.000 €
     - Sport e tempo libero: 3.000 €
     - Auto e mobilità: 5.000 €
     - Utenze: 5.000 €
     - Spesa: 8.000 €

## Pagina "Profilo"
- Una pagina chiamata **"Profilo"** raccoglie il profilo del cliente.
- Contiene il **questionario** con le domande caricate dalla pagina di amministrazione, a cui l'utente risponde.
- In fondo al questionario Marco indica **quanto spende all'anno per ogni dimensione di spesa** (Casa, Sport e tempo libero, Auto e mobilità, Utenze, Spesa): è la sua "spesa corrente". _(Decisione dell'utente.)_
- Il **risultato del profilo** è in una pagina chiamata **"Analisi di spesa"**, raggiungibile **anche dal menu hamburger**.

## Pagina "Analisi di spesa"
- È la pagina **di maggiore effetto grafico** ed è la **prima che si vede dopo l'accesso** (login + MFA).
- Ha **due sezioni affiancate**:
  - **sinistra**: un **diagramma a ragnatela (radar) navigabile**, con un ramo per ogni dimensione di spesa;
  - **destra**: un box testuale il cui contenuto **cambia quando il mouse passa sopra un ramo** della ragnatela.
- Il box di destra ha tre sezioni:
  1. **Spesa corrente**: la somma delle spese di Marco;
  2. **Spesa campione**: la somma delle spese previste per persone con il profilo di Marco, secondo i dati caricati dall'Amministrazione;
  3. **Da sapere**: testi statici, inventati, scelti in base ai dati (dimensione e differenza rispetto al campione). Sono **educativi**: spiegano cosa include la voce e cosa significa la differenza, **senza dire cosa fare**. _(Decisione dell'utente: in origine "Consigli", rinominata per rispettare il vincolo del tema.)_
- Esempio approvato dall'utente (ramo "Auto e mobilità"):
  ```
  Spesa corrente: 6.200 €
  Spesa campione: 5.000 €
  Da sapere
  • Questa voce include carburante, assicurazione, bollo e manutenzione.
  • Spendi 1.200 € in più della media di persone con un profilo come il tuo.
  • Una parte di queste spese è fissa (bollo, assicurazione): si ripete ogni anno con lo stesso importo.
  ```

- **Primo accesso senza profilo compilato**: dopo l'accesso si va alla pagina **Profilo** invece che ad Analisi di spesa. _(Decisione dell'utente.)_
- La ragnatela deve cambiare il box anche con **tastiera (focus) e clic/tocco**, non solo con il mouse. _(Proposta dell'orchestratore per l'accessibilità.)_

## Menu hamburger (in cima alla pagina)
Voci: **Profilo**, **Analisi di spesa**, **Amministrazione**.

## Fonti istituzionali ("Dove verificare")
- Nella sezione dei suggerimenti l'app rimanda a **fonti istituzionali**: MIMIT Osservaprezzi carburanti, IVASS Preventivass, ARERA Portale Offerte, Banca d'Italia "L'economia per tutti". _(Decisione dell'utente: rimando neutro "Dove verificare", senza azioni correttive esplicite, per rispettare il vincolo del tema. Integrazioni automatiche dei dati e consigli generati da AI restano sviluppi futuri.)_

## Altro
_(in raccolta)_

## Risposte dell'utente alle domande dell'analista (Fase 0a)
1. **Score**: ogni opzione o fascia di risposta ha il suo punteggio (es. Età 18-35 = 2, 36-50 = 3; Affitto = 1, Proprietà = 2).
2. **Combinazione**: si sommano gli score di tutte le risposte; il totale cade in una fascia di punteggio, e ogni fascia ha i suoi importi per dimensione. Se il totale non cade in nessuna fascia, si usa la fascia più vicina e l'app lo dichiara.
3. **Periodo**: importi **annuali** in tutta l'app, sia quelli inseriti da Marco sia quelli mostrati.
4. **Soglia "in linea con la media"**: differenza entro **±10%** della spesa campione.
5. **Salvataggio**: profilo e spese salvati **uno per username**. Chi rientra con lo stesso username trova il Profilo compilato e va subito ad Analisi di spesa; un username nuovo va al Profilo.
6. **Ragnatela**: due forme sovrapposte, "la tua spesa" e "spesa campione", con valori in euro e legenda.
7. **Box senza ramo selezionato**: totali di tutte le dimensioni (corrente e campione) e un "Da sapere" generale sul confronto totale.
8. **Campi obbligatori**: tutte le domande e tutte le 5 spese. Spese in numeri interi da 0 a 999.999 €, niente negativi.

Ipotesi dell'analista accettate per silenzio: pagina Amministrazione accessibile a chiunque abbia fatto l'accesso (ruoli fuori ambito); "con chi ti confronti" costruito dalle risposte di Marco; "Da sapere" = testi fissi per dimensione × esito (sopra / in linea / sotto) + frase con la cifra + disclaimer e fonte.

## Pagina iniziale
- Dopo l'accesso (login + MFA) si va **sempre alla pagina Profilo**, anche se il profilo e' gia' compilato. _(Decisione dell'utente; sostituisce la regola "profilo completo -> Analisi di spesa".)_
