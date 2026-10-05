---
description: Avvia una fase del flusso di lavoro (0a, 0b, 0c, 0d, 1, 2, 3, 4, 5a, 5b) dopo aver verificato i prerequisiti
argument-hint: <fase: 0a | 0b | 0c | 0d | 1 | 2 | 3 | 4 | 5a | 5b>
---

Fase richiesta: **$ARGUMENTS**

1. Leggi `agents/stato/avanzamento.md`.
2. Controlla i prerequisiti nella tabella "Flusso di lavoro" di `agents/CLAUDE.md`:
   - la fase precedente è completata;
   - se la fase precedente ha un controllo **STOP**, l'approvazione dell'utente è registrata in "Decisioni dell'utente".
   Se un prerequisito manca, **non avviare** la fase: spiega in una frase cosa manca e fermati.
3. Avvia l'agente o gli agenti della fase (in parallelo quando la tabella lo indica), passando **solo** gli input elencati: percorsi di file e modalità, non contenuti copiati.
4. Leggi soltanto il blocco di protocollo finale di ogni agente (ESITO / FILE_PRODOTTI / SINTESI / DOMANDE_PER_UTENTE / PROBLEMI_APERTI).
5. In base all'ESITO:
   - `COMPLETATO` → aggiorna `agents/stato/avanzamento.md` (fase, orario, esito) e proponi la fase successiva;
   - `SERVE_UTENTE` → porta le domande all'utente, registra le risposte nel file indicato dall'agente e riavvia l'agente;
   - `BLOCCATO` → applica le regole "Cicli di correzione e limiti" di `agents/CLAUDE.md`.
6. Chiudi con il riepilogo nel formato del comando `/stato`.
