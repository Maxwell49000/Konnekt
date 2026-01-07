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

import com.example.reseau_social.dtos.CreateUtilisateurDTO;
import com.example.reseau_social.models.Skill;
import com.example.reseau_social.models.Utilisateur;
import com.example.reseau_social.services.SkillService;
import com.example.reseau_social.services.UtilisateurService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private SkillService skillService;

    // CREATE
    @PostMapping
    public ResponseEntity<Utilisateur> createUtilisateur(@Valid @RequestBody CreateUtilisateurDTO dto) {
        try {
            Utilisateur utilisateur = new Utilisateur(dto.getNom(), dto.getPrenom(), dto.getEmail());
            utilisateur.setTitreProfessionnel(dto.getTitreProfessionnel());
            utilisateur.setResume(dto.getResume());
            utilisateur.setVisibiliteProfil(dto.getVisibiliteProfil());
            Utilisateur created = utilisateurService.createUtilisateur(utilisateur);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // READ - Get all
    @GetMapping
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();
        return ResponseEntity.ok(utilisateurs);
    }

    // READ - Get all visible
    @GetMapping("/visible")
    public ResponseEntity<List<Utilisateur>> getAllVisibleProfiles() {
        List<Utilisateur> utilisateurs = utilisateurService.getAllVisibleProfiles();
        return ResponseEntity.ok(utilisateurs);
    }

    // READ - Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Integer id) {
        Optional<Utilisateur> utilisateur = utilisateurService.getUtilisateurById(id);
        return utilisateur.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // READ - Get by Email
    @GetMapping("/email/{email}")
    public ResponseEntity<Utilisateur> getUtilisateurByEmail(@PathVariable String email) {
        Optional<Utilisateur> utilisateur = utilisateurService.getUtilisateurByEmail(email);
        return utilisateur.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // READ - Search
    @GetMapping("/search")
    public ResponseEntity<List<Utilisateur>> search(@RequestParam String query) {
        List<Utilisateur> utilisateurs = utilisateurService.searchByNomOrPrenom(query);
        return ResponseEntity.ok(utilisateurs);
    }

    // READ - Find by name
    @GetMapping("/nom/{nom}")
    public ResponseEntity<List<Utilisateur>> findByNom(@PathVariable String nom) {
        List<Utilisateur> utilisateurs = utilisateurService.findByNom(nom);
        return ResponseEntity.ok(utilisateurs);
    }

    // READ - Find by first name
    @GetMapping("/prenom/{prenom}")
    public ResponseEntity<List<Utilisateur>> findByPrenom(@PathVariable String prenom) {
        List<Utilisateur> utilisateurs = utilisateurService.findByPrenom(prenom);
        return ResponseEntity.ok(utilisateurs);
    }

    // READ - Find by skill
    @GetMapping("/skill/{skillId}")
    public ResponseEntity<List<Utilisateur>> findBySkill(@PathVariable Integer skillId) {
        List<Utilisateur> utilisateurs = utilisateurService.findBySkill(skillId);
        return ResponseEntity.ok(utilisateurs);
    }

    // READ - Find by min skills
    @GetMapping("/minSkills/{minSkills}")
    public ResponseEntity<List<Utilisateur>> findByMinSkills(@PathVariable int minSkills) {
        List<Utilisateur> utilisateurs = utilisateurService.findByMinSkills(minSkills);
        return ResponseEntity.ok(utilisateurs);
    }

    // READ - Find by min connections
    @GetMapping("/minConnections/{minConnections}")
    public ResponseEntity<List<Utilisateur>> findByMinConnections(@PathVariable int minConnections) {
        List<Utilisateur> utilisateurs = utilisateurService.findByMinConnections(minConnections);
        return ResponseEntity.ok(utilisateurs);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> updateUtilisateur(@PathVariable Integer id, @RequestBody Utilisateur utilisateurDetails) {
        try {
            Utilisateur updated = utilisateurService.updateUtilisateur(id, utilisateurDetails);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // UPDATE - Visibility
    @PutMapping("/{id}/visibility")
    public ResponseEntity<Utilisateur> updateVisibility(@PathVariable Integer id, @RequestParam Boolean visible) {
        try {
            Utilisateur updated = utilisateurService.updateVisibility(id, visible);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // UPDATE - Skills
    @PutMapping("/{id}/skills")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Utilisateur> updateSkills(@PathVariable Integer id, @RequestBody List<Integer> skillIds) {
        try {
            java.util.Set<Skill> skills = new java.util.HashSet<>();
            for (Integer skillId : skillIds) {
                skillService.getSkillById(skillId).ifPresent(skills::add);
            }
            Utilisateur updated = utilisateurService.updateSkills(id, skills);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Integer id) {
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    // STATISTICS
    @GetMapping("/stats/countVisible")
    public ResponseEntity<Long> countVisibleProfiles() {
        long count = utilisateurService.countVisibleProfiles();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/countAll")
    public ResponseEntity<Long> countAllUtilisateurs() {
        long count = utilisateurService.countAllUtilisateurs();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = utilisateurService.emailExists(email);
        return ResponseEntity.ok(exists);
    }
}
