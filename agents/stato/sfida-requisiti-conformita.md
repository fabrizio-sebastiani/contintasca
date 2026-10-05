# Sfida ai requisiti – conformità

| ID | Riferimento | Regola violata (sezione della skill) | Obiezione (1-2 righe) | Proposta conforme | Risposta analista | Esito |
|---|---|---|---|---|---|---|
| OB-C-01 | RB-23, RB-04, S-04 | Fedeltà ai dati – Chiarezza (sez. 1-2): la domanda "Sesso [M/F]" è binaria e introduce il rischio che l'utente interpreti l'app come un tool che associa genere e spese (stereotipo). | La domanda non consente a chi non si identifica in M/F di completare il profilo. Il testo non specifica che serve solo per il matching statistico. | Aggiungere a RB-04 (campo domanda) la specifica: "Se il file contiene una domanda di tipo 'scelta', l'app mostra solo le opzioni del file. Se una domanda riguarda dati sensibili (genere, reddito), il frontend può aggiungere una spiegazione: 'Questi dati servono solo per trovare il tuo profilo statistico. Non determinano giudizi su di te.' Alternativamente, proporre opzioni più inclusive (es. 'Preferisco non rispondere') nel file di esempio in RB-23, oppure rivedere la domanda con categorie di profiling non binarie." | La domanda resta (l'ha chiesta l'utente). RB-23: opzioni M / F / "Preferisco non rispondere" (punteggio 0, risposta valida). Spiegazione obbligatoria nel file: "Serve solo a trovare il tuo gruppo nei dati. Non è un giudizio su di te." Nuovo vincolo in sezione 7: nessun testo collega il sesso a una voce di spesa. Le opzioni vengono solo dal file (già in S-04). | ACCETTATA |
| — | — | — | — | — | — | — |

## Note
- **RB-17, RB-19**: tutte le frasi verificate, max 20 parole, nessuna espressione vietata, nessun giudizio.
- **Disclaimer, Fonte, Profilo approssimato**: presenti e conformi a `linguaggio-semplice` sezione 4.
- **CA-06, CA-07, CA-08**: template e frasi verificati per conformità.
