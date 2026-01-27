package com.example.reseau_social.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExperienceDTO {
    private Integer idExperience;

    @NotBlank(message = "Le poste est requis")
    private String poste;

    @NotBlank(message = "L'entreprise est requise")
    private String entreprise;

    @NotNull(message = "La date de début est requise")
    private LocalDate dateDebut;

    private LocalDate dateFin;
    private String description;

    // Constructors
    public ExperienceDTO() {
    }

    public ExperienceDTO(String poste, String entreprise, LocalDate dateDebut) {
        this.poste = poste;
        this.entreprise = entreprise;
        this.dateDebut = dateDebut;
    }

    public ExperienceDTO(Integer idExperience, String poste, String entreprise, LocalDate dateDebut, LocalDate dateFin, String description) {
        this.idExperience = idExperience;
        this.poste = poste;
        this.entreprise = entreprise;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
    }

    // Getters and Setters
    public Integer getIdExperience() {
        return idExperience;
    }

    public void setIdExperience(Integer idExperience) {
        this.idExperience = idExperience;
    }

    // Alias getter/setter for JSON compatibility with `id`
    public Integer getId() {
        return this.idExperience;
    }

    public void setId(Integer id) {
        this.idExperience = id;
    }

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
