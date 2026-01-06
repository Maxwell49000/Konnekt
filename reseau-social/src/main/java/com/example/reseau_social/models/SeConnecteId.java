package com.example.reseau_social.models;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class SeConnecteId implements Serializable {
    private Integer idDemandeur;
    private Integer idDestinataire;

    // Constructors
    public SeConnecteId() {
    }

    public SeConnecteId(Integer idDemandeur, Integer idDestinataire) {
        this.idDemandeur = idDemandeur;
        this.idDestinataire = idDestinataire;
    }

    // Getters and Setters
    public Integer getIdDemandeur() {
        return idDemandeur;
    }

    public void setIdDemandeur(Integer idDemandeur) {
        this.idDemandeur = idDemandeur;
    }

    public Integer getIdDestinataire() {
        return idDestinataire;
    }

    public void setIdDestinataire(Integer idDestinataire) {
        this.idDestinataire = idDestinataire;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeConnecteId that = (SeConnecteId) o;
        return Objects.equals(idDemandeur, that.idDemandeur) &&
                Objects.equals(idDestinataire, that.idDestinataire);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDemandeur, idDestinataire);
    }
}
