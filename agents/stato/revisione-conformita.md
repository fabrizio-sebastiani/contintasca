# Revisione conformità – ciclo 1

**Sintesi**: 47 OK, 2 MODIFICA, 0 BLOCCA, 1 TESTO_FUORI_FILE | Disclaimer: presente | Fonte citata: sì

| ID | Chiave in testi.ts | Esito | Regola violata | Proposta di riscrittura |
|---|---|---|---|---|
| RC-01 | profilo.riepilogoErrori | MODIFICA | Fedeltà: quando n=1, il testo legge "Ci sono 1 campi da correggere" (agrammaticale). Deve gestire il singolare/plurale. | Implementare: se n=1, mostrare "C'è 1 campo da correggere."; altrimenti "Ci sono {n} campi da correggere." |
| RC-02 | profilo.aiutoImporto | MODIFICA | Chiarezza: "Scrivi l'importo di un anno intero, solo cifre. Per esempio 12000." contiene due istruzioni combinate (7 + 4 = 11 parole totali); la frase potrebbe essere più concisa. | "Scrivi solo cifre. Esempio: 12000." (5 parole) Oppure mantenere se si ritiene necessaria la specifica "di un anno intero". |
| RC-03 | profile-page.component.ts, riga 114 | TESTO_FUORI_FILE | Il valore 'Profilo' è hardcoded nel componente (`protected readonly menuProfilo = 'Profilo';`), non centralizzato in testi.ts. Viola CLAUDE.md "Regole globali §2": tutti i testi visibili in un unico file. | Aggiungere a testi.ts nel blocco `profilo`: `titoloMenuProfilo: 'Profilo',`. Modificare template riga 19 in `{{ testi.titoloMenuProfilo }}`. |

## Risk & Clarity Note

### Cosa è stato semplificato
- **Chiarezza**: Tutti i testi ≤ 20 parole per frase. Errori in tre parti (cosa / perché / come), es. "L'importo non è valido. Una spesa non può essere sotto zero. Scrivi un numero da 0 a 999999." (15 parole).
- **Voce attiva**: sempre "tu" ("Scrivi", "Hai", "Clicca"), mai passivo.
- **Confronti neutrali**: solo "più della media", "meno della media", "in linea con la media". Nessun giudizio ("troppo", "bene", "male").
- **Tabella B (fraseCifra)**: template corretti, es. "Per <Voce> spendi {D} € all'anno in più della media di persone come te. È il {P}% in più." (17 parole ÷ 2 frasi).
- **ParoleUtili (glossario)**: 3 definizioni, tutte ≤ 18 parole.

### Cosa non è stato alterato
- **Disclaimer**: esatto da SKILL §4 e sezione 7 dei requisiti: "Questo confronto ha solo scopo informativo ed educativo. Non è un consiglio finanziario."
- **Fonte**: esatta, "Dati dimostrativi ispirati all'Indagine ISTAT sulle spese delle famiglie, anno 2024." (anno 2024 approvato dall'utente in DA-02).
- **Frase obbligatoria**: "La media è un riferimento, non un obiettivo: ogni situazione è diversa." (12 parole, conforme).
- **Nessuna espressione vietata** (SKILL §3): verificato in tutti i testi. No "dovresti", "conviene", "taglia", "investimento consigliato", "budget ideale", ecc.
- **RB-18 (Con chi ti confronti)**: fraseConChiTiConfronti genera "Ti confronti con persone con un profilo come il tuo: punteggio del profilo da X a Y. Il tuo punteggio è Z." (17 parole, 2 frasi, conforme).
- **RB-19 (Parole utili)**: termini e definizioni rispettano il limite di 25 parole.

### Come è stata evitata l'ambiguità
- **Tabella A** ("cosa include"): non propone preferenze, solo descrive il contenuto di ogni voce.
- **Tabella C** ("frase-concetto"): spiega i fattori che influenzano la spesa, non giudica. Es. "Una differenza in questa voce può dipendere dalla città e dalla grandezza della casa." (14 parole) vs "Spendi troppo in affitto" (vietato).
- **avvisoApprossimato**: "Nei dati non c'è un gruppo con il tuo punteggio ({Z}). Ti confrontiamo con il gruppo più vicino: punteggio da {X} a {Y}." (16 parole, 2 frasi) – spiega il motivo senza giudizio.
- **Numeri**: euro con punto delle migliaia (312 €), percentuali intere (12%), come da SKILL §1.3.
- **rigaRisposta**: formato "<etichetta>: <valore>" rende trasparente chi è Marco e cosa ha risposto.

### Rischi residui
- **RC-01 (Plurale)**: Se Marco ha 1 solo campo errato, il testo dirà "Ci sono 1 campi" (agrammaticale in italiano). Necessario implementare logica nel componente o in una funzione helper per distinguere singolare/plurale.
- **RC-02 (Concisione opzionale)**: L'aiuto dell'importo potrebbe essere ancora più sintetico ("Scrivi solo cifre, es. 12000."), ma il testo attuale è comunque conforme (11 parole ÷ 2 frasi = max 7 per frase). Non è un blocco.
- **RC-03 (Accentramento testi)**: Il testo "Profilo" in profile-page.component.ts è un'anomalia. Non compromette il significato per Marco, ma crea rischi di incoerenza se il testo verrà tradotto o modificato senza aggiornare il componente.
- **Nessuna minaccia di consiglio finanziario**: verificato ogni testo. Non ci sono indicazioni su dove tagliare, budget consigliati, prodotti da scegliere, banche da preferire.

