package it.hagenthon.backend.data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

/** Al massimo una riga: lo stato dell'ultimo caricamento riuscito. */
@Entity
@Table(name = "data_load")
public class DataLoad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    @Column(length = 255)
    private String nomeFile;

    private LocalDateTime caricatoIl;

    private int numeroDomande;

    private int numeroFasce;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public LocalDateTime getCaricatoIl() {
        return caricatoIl;
    }

    public void setCaricatoIl(LocalDateTime caricatoIl) {
        this.caricatoIl = caricatoIl;
    }

    public int getNumeroDomande() {
        return numeroDomande;
    }

    public void setNumeroDomande(int numeroDomande) {
        this.numeroDomande = numeroDomande;
    }

    public int getNumeroFasce() {
        return numeroFasce;
    }

    public void setNumeroFasce(int numeroFasce) {
        this.numeroFasce = numeroFasce;
    }
}
