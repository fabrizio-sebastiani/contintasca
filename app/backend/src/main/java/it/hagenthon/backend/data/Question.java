package it.hagenthon.backend.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 30)
    @Column(unique = true, nullable = false, length = 30)
    private String codice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionType tipo;

    @NotNull
    @Size(max = 40)
    @Column(nullable = false, length = 40)
    private String etichetta;

    @NotNull
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String testo;

    @Size(max = 200)
    @Column(length = 200)
    private String spiegazione;

    private int ordine;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public QuestionType getTipo() {
        return tipo;
    }

    public void setTipo(QuestionType tipo) {
        this.tipo = tipo;
    }

    public String getEtichetta() {
        return etichetta;
    }

    public void setEtichetta(String etichetta) {
        this.etichetta = etichetta;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getSpiegazione() {
        return spiegazione;
    }

    public void setSpiegazione(String spiegazione) {
        this.spiegazione = spiegazione;
    }

    public int getOrdine() {
        return ordine;
    }

    public void setOrdine(int ordine) {
        this.ordine = ordine;
    }
}
