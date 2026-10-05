package it.hagenthon.backend.data;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** {@code GET/POST /api/dati} (S-05, sezione 3.2 e 3.3 del contratto). */
@RestController
@RequestMapping("/api/dati")
public class DataController {

    private final DataImportService service;

    public DataController(DataImportService service) {
        this.service = service;
    }

    @GetMapping
    public DatiAttualiResponse dati() {
        return service.datiAttuali();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CaricamentoResponse carica(@RequestParam("file") MultipartFile file) {
        String nomeFile = file.getOriginalFilename();
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return service.importFile(nomeFile, bytes);
    }
}
