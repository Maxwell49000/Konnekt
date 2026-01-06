package com.example.reseau_social.controllers;

import java.util.List;
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

import com.example.reseau_social.dtos.CreateExperienceDTO;
import com.example.reseau_social.models.Experience;
import com.example.reseau_social.services.ExperienceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/experiences")
@CrossOrigin(origins = "*")
public class ExperienceController {

    @Autowired
    private ExperienceService experienceService;

    // CREATE
    @PostMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<Experience> createExperience(@PathVariable Integer utilisateurId, @Valid @RequestBody CreateExperienceDTO dto) {
        try {
            Experience experience = new Experience(dto.getPoste(), dto.getEntreprise(), dto.getDateDebut());
            experience.setDateFin(dto.getDateFin());
            experience.setDescription(dto.getDescription());
            Experience created = experienceService.createExperience(utilisateurId, experience);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // READ - Get all
    @GetMapping
    public ResponseEntity<List<Experience>> getAllExperiences() {
        List<Experience> experiences = experienceService.getAllExperiences();
        return ResponseEntity.ok(experiences);
    }

    // READ - Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<Experience> getExperienceById(@PathVariable Integer id) {
        Optional<Experience> experience = experienceService.getExperienceById(id);
        return experience.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // READ - Get by utilisateur
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Experience>> getExperiencesByUtilisateur(@PathVariable Integer utilisateurId) {
        List<Experience> experiences = experienceService.getExperiencesByUtilisateur(utilisateurId);
        return ResponseEntity.ok(experiences);
    }

    // READ - Get by utilisateur ordered by date
    @GetMapping("/utilisateur/{utilisateurId}/ordered")
    public ResponseEntity<List<Experience>> getExperiencesByUtilisateurOrdered(@PathVariable Integer utilisateurId) {
        List<Experience> experiences = experienceService.getExperiencesByUtilisateurOrderByDateDebut(utilisateurId);
        return ResponseEntity.ok(experiences);
    }

    // READ - Get current experiences
    @GetMapping("/utilisateur/{utilisateurId}/current")
    public ResponseEntity<List<Experience>> getCurrentExperiencesByUtilisateur(@PathVariable Integer utilisateurId) {
        List<Experience> experiences = experienceService.getCurrentExperiencesByUtilisateur(utilisateurId);
        return ResponseEntity.ok(experiences);
    }

    // READ - Get active experiences
    @GetMapping("/utilisateur/{utilisateurId}/active")
    public ResponseEntity<List<Experience>> getActiveExperiencesByUtilisateur(@PathVariable Integer utilisateurId) {
        List<Experience> experiences = experienceService.getActiveExperiencesByUtilisateur(utilisateurId);
        return ResponseEntity.ok(experiences);
    }

    // READ - Search by entreprise
    @GetMapping("/entreprise/{entreprise}")
    public ResponseEntity<List<Experience>> findByEntreprise(@PathVariable String entreprise) {
        List<Experience> experiences = experienceService.findByEntreprise(entreprise);
        return ResponseEntity.ok(experiences);
    }

    // READ - Search by poste
    @GetMapping("/poste/{poste}")
    public ResponseEntity<List<Experience>> findByPoste(@PathVariable String poste) {
        List<Experience> experiences = experienceService.findByPoste(poste);
        return ResponseEntity.ok(experiences);
    }

    // READ - Search
    @GetMapping("/search")
    public ResponseEntity<List<Experience>> search(@RequestParam String query) {
        List<Experience> experiences = experienceService.searchByEntrepriseOrPoste(query);
        return ResponseEntity.ok(experiences);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Experience> updateExperience(@PathVariable Integer id, @RequestBody Experience experienceDetails) {
        try {
            Experience updated = experienceService.updateExperience(id, experienceDetails);
            return ResponseEntity.ok(updated);
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
}
