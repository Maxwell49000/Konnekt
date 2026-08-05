package com.example.reseau_social.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.reseau_social.dtos.ExperienceDTO;
import com.example.reseau_social.models.Experience;
import com.example.reseau_social.services.ExperienceService;

import jakarta.validation.Valid;

// Controller class for managing experiences
@RestController
@RequestMapping("/api/experiences")
@CrossOrigin(origins = "*")
public class ExperienceController {

    @Autowired
    private ExperienceService experienceService;

    // CREATE
    @PostMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<ExperienceDTO> createExperience(@PathVariable Integer utilisateurId, @Valid @RequestBody ExperienceDTO dto) {
        try {
            Experience experience = new Experience(dto.getPoste(), dto.getEntreprise(), dto.getDateDebut());
            experience.setDateFin(dto.getDateFin());
            experience.setDescription(dto.getDescription());
            Experience created = experienceService.createExperience(utilisateurId, experience);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // READ - Get all
    @GetMapping
    public ResponseEntity<java.util.List<ExperienceDTO>> getAllExperiences() {
        java.util.List<Experience> experiences = experienceService.getAllExperiences();
        java.util.List<ExperienceDTO> dtos = experiences.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    // READ - Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<ExperienceDTO> getExperienceById(@PathVariable Integer id) {
        Optional<Experience> experience = experienceService.getExperienceById(id);
        return experience.map(e -> ResponseEntity.ok(toDTO(e)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // READ - Get by utilisateur
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<java.util.List<ExperienceDTO>> getExperiencesByUtilisateur(@PathVariable Integer utilisateurId) {
        java.util.List<Experience> experiences = experienceService.getExperiencesByUtilisateur(utilisateurId);
        java.util.List<ExperienceDTO> dtos = experiences.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    // READ - Get by utilisateur ordered by date
    @GetMapping("/utilisateur/{utilisateurId}/ordered")
    public ResponseEntity<java.util.List<ExperienceDTO>> getExperiencesByUtilisateurOrdered(@PathVariable Integer utilisateurId) {
        java.util.List<Experience> experiences = experienceService.getExperiencesByUtilisateurOrderByDateDebut(utilisateurId);
        java.util.List<ExperienceDTO> dtos = experiences.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    // READ - Get current experiences
    @GetMapping("/utilisateur/{utilisateurId}/current")
    public ResponseEntity<java.util.List<ExperienceDTO>> getCurrentExperiencesByUtilisateur(@PathVariable Integer utilisateurId) {
        java.util.List<Experience> experiences = experienceService.getCurrentExperiencesByUtilisateur(utilisateurId);
        java.util.List<ExperienceDTO> dtos = experiences.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    // READ - Get active experiences
    @GetMapping("/utilisateur/{utilisateurId}/active")
    public ResponseEntity<java.util.List<ExperienceDTO>> getActiveExperiencesByUtilisateur(@PathVariable Integer utilisateurId) {
        java.util.List<Experience> experiences = experienceService.getActiveExperiencesByUtilisateur(utilisateurId);
        java.util.List<ExperienceDTO> dtos = experiences.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    // READ - Search by entreprise
    @GetMapping("/entreprise/{entreprise}")
    public ResponseEntity<java.util.List<ExperienceDTO>> findByEntreprise(@PathVariable String entreprise) {
        java.util.List<Experience> experiences = experienceService.findByEntreprise(entreprise);
        java.util.List<ExperienceDTO> dtos = experiences.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    // READ - Search by poste
    @GetMapping("/poste/{poste}")
    public ResponseEntity<java.util.List<ExperienceDTO>> findByPoste(@PathVariable String poste) {
        java.util.List<Experience> experiences = experienceService.findByPoste(poste);
        java.util.List<ExperienceDTO> dtos = experiences.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    // READ - Search
    @GetMapping("/search")
    public ResponseEntity<java.util.List<ExperienceDTO>> search(@RequestParam String query) {
        java.util.List<Experience> experiences = experienceService.searchByEntrepriseOrPoste(query);
        java.util.List<ExperienceDTO> dtos = experiences.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ExperienceDTO> updateExperience(@PathVariable Integer id, @RequestBody Experience experienceDetails) {
        try {
            Experience updated = experienceService.updateExperience(id, experienceDetails);
            return ResponseEntity.ok(toDTO(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExperience(@PathVariable Integer id) {
        experienceService.deleteExperience(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE - All by utilisateur
    @DeleteMapping("/utilisateur/{utilisateurId}/all")
    public ResponseEntity<Void> deleteAllExperiencesByUtilisateur(@PathVariable Integer utilisateurId) {
        experienceService.deleteAllExperiencesByUtilisateur(utilisateurId);
        return ResponseEntity.noContent().build();
    }

    // STATISTICS
    @GetMapping("/stats/count/utilisateur/{utilisateurId}")
    public ResponseEntity<Long> countExperiencesByUtilisateur(@PathVariable Integer utilisateurId) {
        long count = experienceService.countExperiencesByUtilisateur(utilisateurId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/countAll")
    public ResponseEntity<Long> countAllExperiences() {
        long count = experienceService.countAllExperiences();
        return ResponseEntity.ok(count);
    }

    // Mapper helper
    private ExperienceDTO toDTO(Experience e) {
        if (e == null) return null;
        ExperienceDTO dto = new ExperienceDTO();
        dto.setIdExperience(e.getIdExperience());
        dto.setPoste(e.getPoste());
        dto.setEntreprise(e.getEntreprise());
        dto.setDateDebut(e.getDateDebut());
        dto.setDateFin(e.getDateFin());
        dto.setDescription(e.getDescription());
        return dto;
    }
}
