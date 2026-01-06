package com.example.reseau_social.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateExperienceDTO {
    @NotBlank(message = "Le poste est requis")
    private String poste;

    @NotBlank(message = "L'entreprise est requise")
    private String entreprise;

    @NotNull(message = "La date de début est requise")
    private LocalDate dateDebut;

    private LocalDate dateFin;

    private String description;

    // Constructors
    public CreateExperienceDTO() {
    }

    public CreateExperienceDTO(String poste, String entreprise, LocalDate dateDebut) {
        this.poste = poste;
        this.entreprise = entreprise;
        this.dateDebut = dateDebut;
    }

    // Getters and Setters
    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public String getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(String entreprise) {
        this.entreprise = entreprise;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
