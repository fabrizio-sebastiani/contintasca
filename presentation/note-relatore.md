# Note per il relatore — "In tasca mia"

> Pubblico business, non tecnico. La parte principale (slide 1-6) non usa termini tecnici; l'Annex (slide 8-12, dopo il separatore) è per chi vuole vedere come è stata costruita l'applicazione. Il segnaposto "n.d." nella slide A4 (criteri di accettazione) verrà sostituito quando il tester avrà concluso la verifica.

## Slide 1 — Apertura
Iniziamo con una domanda semplice e diretta: sai davvero dove finiscono i tuoi soldi ogni anno? La maggior parte delle persone risponde di no, o risponde a sensazioni più che a numeri. "In tasca mia" nasce per rispondere a questa domanda con fatti, non con sensazioni.

## Slide 2 — Marco
Marco ha 34 anni, vive da solo in affitto a Milano, e non è un esperto di economia — come la maggior parte di noi. Il problema non è che non sappia quanto spende: è che non ha un termine di paragone, e a fine mese fatica a capire perché non riesce a mettere via qualcosa.

## Slide 3 — La soluzione
La soluzione è semplice e in tre passi: un breve profilo, un confronto visivo con persone con caratteristiche simili, e spiegazioni chiare su cosa significano le differenze. Il messaggio centrale è "capire prima di decidere": l'app non dice mai a Marco cosa fare con i suoi soldi.

## Slide 4 — Prima e dopo
Prendiamo l'esempio dell'auto: prima, Marco vede solo un numero, 6.200 € l'anno, senza sapere se è normale. Dopo, vede lo stesso numero accanto alla media di persone simili a lui, 5.000 €, con una spiegazione del perché c'è questa differenza. Qui si passa alla demo dal vivo.

## Slide 5 — Connessioni (visione)
Questa è una visione per il futuro, non parte della demo di oggi: l'app potrebbe portare Marco verso strumenti pubblici e di mercato dove verificare da solo le proprie condizioni — bollette, polizze, conto corrente. Sono tutte integrazioni dimostrative: la scelta resta sempre sua.

## Slide 6 — Chiusura
Il valore per Marco non è un consiglio, è consapevolezza: capire le proprie spese con i propri occhi, e decidere con più informazioni in mano. "In tasca mia" aiuta a capire prima, decidere dopo — e la decisione resta sempre dell'utente.

## Slide 7 — Separatore Annex
Questa slide introduce la parte più tecnica della presentazione, per chi è curioso di sapere come è stata costruita l'applicazione: un sistema di agenti specializzati, con controlli e approvazioni a ogni passo.

## Slide A1 — La squadra di agenti
Il progetto non è scritto da una sola intelligenza artificiale: è un flusso di agenti specializzati, coordinati da un orchestratore che è l'unico a parlare con l'utente. Ci sono due approvazioni obbligatorie dell'utente, e due cicli di correzione con limiti espliciti per evitare loop infiniti: finora 0 cicli usati su 3 in verifica, 1 su 2 nelle revisioni.

## Slide A2 — Le istruzioni
Ogni file agente segue la stessa struttura — Ruolo, Input, Passi, Formato, Vincoli, Cosa NON fare, Quando chiedere — il che li rende prevedibili e controllabili. L'estratto mostrato è preso parola per parola dal file che istruisce lo sviluppatore backend.

## Slide A3 — Controllo reciproco e robustezza
Gli agenti si controllano a vicenda: l'architetto e il revisore di conformità hanno sollevato 7 obiezioni sui requisiti, tutte accettate e integrate prima di scrivere codice. Il sistema ha anche limiti chiari sui cicli di correzione, e un'importazione dati resiliente: una riga non valida non blocca le altre.

## Slide A4 — Efficienza e qualità tecnica
Non tutti gli agenti usano lo stesso modello: i più potenti dove serve ragionare e decidere, modelli più leggeri per compiti ripetitivi. 35 test automatici sul backend sono tutti superati, la build del frontend è verde. La revisione di sicurezza non ha trovato nulla di bloccante o grave: un solo rilievo di gravità media, una dipendenza non prevista dal piano ma innocua perché la funzione resta disattivata — un limite noto, non un rischio. I criteri di accettazione arriveranno a verifica completata.

## Slide A5 — Rischi, chiarezza e ruolo dell'AI
La revisione di conformità ha controllato ogni testo visibile a Marco: su 50 verifiche, solo 3 piccoli miglioramenti, già corretti, nessun blocco, nessuna contestazione. Gli agenti hanno scritto il progetto, ma ogni decisione importante — tema, nome, opzioni sensibili — è stata presa e approvata dall'utente. I limiti sono dichiarati fin dall'inizio: login e dati sono simulati o inventati per la demo.
