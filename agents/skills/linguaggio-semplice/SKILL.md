---
name: linguaggio-semplice
description: Regole per scrivere ogni testo visibile all'utente dell'app (persona con bassa alfabetizzazione finanziaria) - chiarezza, fedeltà ai dati, espressioni vietate perché suonano come consigli finanziari, disclaimer obbligatorio. Da usare per scrivere o revisionare testi, etichette, messaggi di errore e glossario.
---

# Linguaggio semplice e nessun consiglio

Il lettore è **Marco**: capisce le cifre, ma non il gergo finanziario, e si scoraggia davanti a testi lunghi. L'app gli mostra **fatti** e gli spiega **concetti**. Non gli dice mai **cosa fare** con i suoi soldi.

## 1. Chiarezza
1. Massimo **20 parole** per frase, una sola idea per frase.
2. Dai del **tu** e usa la voce attiva ("Hai inserito…", non "Sono stati inseriti…").
3. Importi in euro **arrotondati all'unità** con il simbolo dopo la cifra (`312 €`); percentuali **intere** (`12%`).
4. Nessun termine tecnico senza spiegazione: se serve, deve comparire nel **glossario** con una definizione di massimo 25 parole.
5. Messaggi di errore in tre parti: **cosa è successo / perché / cosa puoi fare nell'app** (es. "Il file non è stato caricato. Non è un file CSV. Scegli un file che finisce con .csv.").

## 2. Fedeltà ai dati (semplificare senza tradire)
1. Confronti **neutri**: "più della media", "meno della media", "in linea con la media". Mai "troppo", "poco", "bene", "male", "eccessivo", "virtuoso".
2. Il **segno** e l'**ordine di grandezza** della differenza non cambiano mai con l'arrotondamento: se la differenza arrotondata è 0 €, si scrive "in linea con la media".
3. Va sempre detto **con chi** si confronta: "famiglie come la tua: persona sola sotto i 35 anni, Nord-ovest".
4. Se il profilo usato è **approssimato** (profilo esatto non disponibile), va detto esplicitamente, indicando il profilo usato.
5. Una media **non è un obiettivo**: almeno una volta per schermata di risultato compare la frase "La media è un riferimento, non un obiettivo: ogni situazione è diversa."

## 3. Espressioni vietate (consiglio finanziario)
Vietate in ogni forma e coniugazione:
- dovresti, devi, ti consiglio, ti suggeriamo, è consigliabile, conviene, è meglio, ti conviene
- risparmia, taglia, riduci, elimina, spendi meno, spendi di più
- investi, investimento consigliato, compra, vendi, scegli (riferito a prodotti o servizi finanziari), passa a (operatore, banca, fornitore)
- obiettivo di spesa, budget ideale, spesa corretta, spesa giusta

Ammesso: descrivere **concetti** in modo generale (es. "Una spesa fissa è una spesa che si ripete ogni mese con lo stesso importo, come l'affitto.").

## 4. Testi obbligatori
- **Disclaimer**, visibile in ogni schermata di risultato, testo esatto:
  "Questo confronto ha solo scopo informativo ed educativo. Non è un consiglio finanziario."
- **Fonte**, sotto ogni confronto: "Dati dimostrativi ispirati all'Indagine ISTAT sulle spese delle famiglie, anno 2024." (i dati della demo sono inventati: non vanno mai presentati come dati ISTAT reali)

## 5. Esempi
| ❌ No | ✅ Sì |
|---|---|
| Spendi troppo in trasporti, dovresti usare i mezzi pubblici. | Per i trasporti spendi 720 € all'anno più della media di persone con un profilo come il tuo. |
| Ottimo! Sei un risparmiatore virtuoso. | In totale spendi 1.800 € all'anno meno della media di persone con un profilo come il tuo. |
| Errore 400: validation failed on field importo. | L'importo non è valido. Puoi usare solo numeri, senza il simbolo €. Correggi il campo evidenziato. |
| Abitazione: +18% vs benchmark. | Per la casa spendi il 18% in più della media. In questa voce ISTAT conta anche il valore della casa di proprietà. |
