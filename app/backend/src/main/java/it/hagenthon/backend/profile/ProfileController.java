package it.hagenthon.backend.profile;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.hagenthon.backend.common.UserNames;

/** {@code GET/PUT /api/profilo} (S-04, sezione 3.4 e 3.5 del contratto). */
@RestController
@RequestMapping("/api/profilo")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping
    public ProfileResponse get(@RequestParam String nomeUtente) {
        String normalizzato = UserNames.requireValid(nomeUtente);
        return service.get(normalizzato);
    }

    @PutMapping
    public SalvataggioResponse put(@RequestParam String nomeUtente, @RequestBody ProfileSaveRequest request) {
        String normalizzato = UserNames.requireValid(nomeUtente);
        return service.save(normalizzato, request.risposte(), request.importi());
    }
}
