package it.hagenthon.backend.profile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(unique = true, nullable = false, length = 50)
    private String nomeUtente;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "profile_answer", joinColumns = @JoinColumn(name = "profile_id"))
    @MapKeyColumn(name = "codice_domanda")
    @Column(name = "valore", length = 20)
    private Map<String, String> risposte = new HashMap<>();

    @Min(0)
    @Max(999999)
    private int casa;

    @Min(0)
    @Max(999999)
    private int sportTempoLibero;

    @Min(0)
    @Max(999999)
    private int autoMobilita;

    @Min(0)
    @Max(999999)
    private int utenze;

    @Min(0)
    @Max(999999)
    private int spesa;

    private LocalDateTime salvatoIl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public Map<String, String> getRisposte() {
        return risposte;
    }

    public void setRisposte(Map<String, String> risposte) {
        this.risposte = risposte;
    }

    public int getCasa() {
        return casa;
    }

    public void setCasa(int casa) {
        this.casa = casa;
    }

    public int getSportTempoLibero() {
        return sportTempoLibero;
    }

    public void setSportTempoLibero(int sportTempoLibero) {
        this.sportTempoLibero = sportTempoLibero;
    }

    public int getAutoMobilita() {
        return autoMobilita;
    }

    public void setAutoMobilita(int autoMobilita) {
        this.autoMobilita = autoMobilita;
    }

    public int getUtenze() {
        return utenze;
    }

    public void setUtenze(int utenze) {
        this.utenze = utenze;
    }

    public int getSpesa() {
        return spesa;
    }

    public void setSpesa(int spesa) {
        this.spesa = spesa;
    }

    public LocalDateTime getSalvatoIl() {
        return salvatoIl;
    }

    public void setSalvatoIl(LocalDateTime salvatoIl) {
        this.salvatoIl = salvatoIl;
    }
}
