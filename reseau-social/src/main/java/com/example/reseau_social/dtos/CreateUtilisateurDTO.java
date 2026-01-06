package com.example.reseau_social.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateUtilisateurDTO {
    @NotBlank(message = "Le nom est requis")
    private String nom;

    @NotBlank(message = "Le prénom est requis")
    private String prenom;

    @NotBlank(message = "L'email est requis")
    @Email(message = "L'email doit être valide")
    private String email;

    private String titreProfessionnel;
    private String resume;
    private Boolean visibiliteProfil;

    // Constructors
    public CreateUtilisateurDTO() {
    }

    public CreateUtilisateurDTO(String nom, String prenom, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.visibiliteProfil = true;
    }

    // Getters and Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitreProfessionnel() {
        return titreProfessionnel;
    }

    public void setTitreProfessionnel(String titreProfessionnel) {
        this.titreProfessionnel = titreProfessionnel;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Boolean getVisibiliteProfil() {
        return visibiliteProfil;
    }

    public void setVisibiliteProfil(Boolean visibiliteProfil) {
        this.visibiliteProfil = visibiliteProfil;
    }
}
