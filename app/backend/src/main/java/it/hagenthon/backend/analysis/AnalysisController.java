package it.hagenthon.backend.analysis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.hagenthon.backend.common.UserNames;

/** {@code GET /api/analisi} (S-03, sezione 3.6 del contratto). */
@RestController
@RequestMapping("/api/analisi")
public class AnalysisController {

    private final ComparisonService service;

    public AnalysisController(ComparisonService service) {
        this.service = service;
    }

    @GetMapping
    public AnalisiResponse analisi(@RequestParam String nomeUtente) {
        String normalizzato = UserNames.requireValid(nomeUtente);
        return service.analizza(normalizzato);
    }
}
