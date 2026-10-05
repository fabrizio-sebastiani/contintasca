# Revisione conformità – ciclo 1 (RISOLTO)

**Sintesi ciclo 1**: 47 OK, 0 MODIFICA, 0 BLOCCA, 0 TESTO_FUORI_FILE | Disclaimer: presente | Fonte citata: sì | **Tutti i rilievi risolti**

| ID | Chiave in testi.ts | Esito | Risoluzione |
|---|---|---|---|
| RC-01 | profilo.riepilogoErrori | RISOLTO | Implementato plurale: `(n === 1 ? 'C\'è 1 campo da correggere.' : \`Ci sono ${n} campi da correggere.\`)` (testi.ts:289) ✓ |
| RC-02 | profilo.aiutoImporto | RISOLTO | Accorciato a `'Scrivi solo cifre. Esempio: 12000.'` (5 parole, testi.ts:285) ✓ |
| RC-03 | profile-page.component.ts | RISOLTO | Testo centralizzato: `titoloMenuProfilo: 'Profilo'` in testi.ts:282; template usa `{{ testi.titoloMenuProfilo }}` (profile-page.component.ts:19) ✓ |

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
- **Nessuno**: tutti i problemi del ciclo 1 sono stati risolti.

---

# Ciclo 2 – RB-26 "Dove verificare" (COMPLETATO)

**Sintesi ciclo 2**: 7 OK, 0 MODIFICA, 0 BLOCCA | Fonte MIMIT/IVASS/ARERA/Banca d'Italia: presenti e corrette

| ID | Chiave in testi.ts | Esito | Regola violata | Proposta di riscrittura |
|---|---|---|---|---|
| RC-04 | doveVerificare.titolo | OK | – | – |
| RC-05 | doveVerificare.chiusura | OK | – | – |
| RC-06 | doveVerificare.notaNuovaScheda | OK | – | – |
| RC-07 | linkVerificaPerVoce.BANCA_ITALIA | OK | – | – |
| RC-08 | linkVerificaPerVoce.AUTO_MOBILITA (MIMIT) | OK | – | – |
| RC-09 | linkVerificaPerVoce.AUTO_MOBILITA (IVASS) | OK | – | – |
| RC-10 | linkVerificaPerVoce.UTENZE (ARERA) | OK | – | – |

### Analisi RB-26

**Verifica completata su tutti i testi di RB-26:**

- **RC-04**: "Dove verificare" (2 parole) ✓
- **RC-05**: "Sono servizi pubblici e gratuiti. La scelta resta tua." (8 parole, 2 frasi: 4+4) ✓
- **RC-06**: "Si apre in una nuova scheda." (5 parole) ✓
- **RC-07 (Banca d'Italia)**: "Puoi trovare spiegazioni semplici sul bilancio familiare nel portale «L'economia per tutti» della Banca d'Italia." (16 parole) ✓
- **RC-08 (MIMIT)**: "Puoi vedere i prezzi dei carburanti dei distributori vicino a te sull'Osservatorio prezzi carburanti del MIMIT." (16 parole) ✓
- **RC-09 (IVASS)**: "Puoi confrontare il prezzo dell'assicurazione auto obbligatoria (RC auto) sul preventivatore pubblico IVASS." (14 parole) ✓
- **RC-10 (ARERA)**: "Puoi confrontare le offerte di luce e gas sul Portale Offerte di ARERA, un servizio pubblico e gratuito." (18 parole) ✓

**Conformità a SKILL e requisiti:**
- ✓ **Nessuna espressione vietata** (SKILL §3): "Puoi" è neutro e ammesso, non compaiono "dovresti", "conviene", "scegli", ecc.
- ✓ **Nessun giudizio di valore**: non ci sono "troppo", "bene", "male", "migliore", "peggiore".
- ✓ **Chiarezza**: tutte le frasi ≤ 20 parole.
- ✓ **Neutralità su scelta**: la frase di chiusura "La scelta resta tua" ribadisce che Marco decide autonomamente.
- ✓ **Rimandi solo a servizi pubblici**: MIMIT (Osservatorio prezzi), IVASS (preventivatore pubblico), ARERA (portale offerte), Banca d'Italia (educazione finanziaria). Nessun rimando a banche, provider, assicuratori commerciali.
- ✓ **RB-26 applicata correttamente**: il riquadro "Dove verificare" compare solo per esito SOPRA (incluso "oltre il 10%"), non per totale, "in linea", "sotto".
- ✓ **Link dichiarati**: "Si apre in una nuova scheda" spiega il comportamento di `target="_blank"`.

**Rischi residui ciclo 2:** Nessuno. Tutti i testi RB-26 rispettano i vincoli di conformità.

