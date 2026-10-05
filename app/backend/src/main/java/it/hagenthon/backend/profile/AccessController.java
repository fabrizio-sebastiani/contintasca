package it.hagenthon.backend.profile;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.hagenthon.backend.common.ApiException;
import it.hagenthon.backend.common.ErrorDetail;
import it.hagenthon.backend.common.UserNames;

/** {@code POST /api/accesso} (S-01, RB-01, sezione 3.1 del contratto). */
@RestController
@RequestMapping("/api/accesso")
public class AccessController {

    @PostMapping
    public AccessResponse accedi(@RequestBody(required = false) AccessRequest request) {
        AccessRequest req = request == null ? new AccessRequest(null, null) : request;
        List<ErrorDetail> dettagli = new ArrayList<>();

        String normalizzato = UserNames.normalize(req.nomeUtente());
        if (normalizzato.isEmpty()) {
            dettagli.add(new ErrorDetail("nomeUtente", "CAMPO_MANCANTE", null, null));
        } else if (normalizzato.length() > UserNames.MASSIMO) {
            dettagli.add(new ErrorDetail("nomeUtente", "CAMPO_TROPPO_LUNGO", null, 50));
        }

        String password = req.password();
        if (password == null || password.isEmpty()) {
            dettagli.add(new ErrorDetail("password", "CAMPO_MANCANTE", null, null));
        } else if (password.length() > 100) {
            dettagli.add(new ErrorDetail("password", "CAMPO_TROPPO_LUNGO", null, 100));
        }

        if (!dettagli.isEmpty()) {
            throw new ApiException(400, "VALIDAZIONE", "Dati di accesso non validi", dettagli);
        }
        return new AccessResponse(normalizzato);
    }
}
