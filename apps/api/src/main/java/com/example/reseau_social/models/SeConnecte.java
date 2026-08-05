package com.example.reseau_social.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

// Model for connection requests between users
@Entity
@Table(name = "se_connecte", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_demandeur", "id_destinataire"})
})
public class SeConnecte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demandeur", nullable = false)
    private Utilisateur demandeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_destinataire", nullable = false)
    private Utilisateur destinataire;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutConnexion statut;

    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation;

    // Enum for statut
    public enum StatutConnexion {
        EN_ATTENTE, ACCEPTEE, REFUSEE, BLOQUEE
    }

    // Constructors
    public SeConnecte() {
    }

    public SeConnecte(Utilisateur demandeur, Utilisateur destinataire) {
        this.demandeur = demandeur;
        this.destinataire = destinataire;
        this.statut = StatutConnexion.EN_ATTENTE;
        this.dateCreation = LocalDate.now();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Utilisateur getDemandeur() {
        return demandeur;
    }

    public void setDemandeur(Utilisateur demandeur) {
        this.demandeur = demandeur;
    }

    public Utilisateur getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(Utilisateur destinataire) {
        this.destinataire = destinataire;
    }

    public StatutConnexion getStatut() {
        return statut;
    }

    public void setStatut(StatutConnexion statut) {
        this.statut = statut;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }
}
