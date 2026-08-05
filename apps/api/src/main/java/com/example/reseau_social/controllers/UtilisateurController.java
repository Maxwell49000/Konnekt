package com.example.reseau_social.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.example.reseau_social.dtos.SkillDTO;
import com.example.reseau_social.dtos.UtilisateurDTO;
import com.example.reseau_social.models.Skill;
import com.example.reseau_social.models.Utilisateur;
import com.example.reseau_social.repositories.UtilisateurRepository;
import com.example.reseau_social.services.SkillService;
import com.example.reseau_social.services.UtilisateurService;

import jakarta.validation.Valid;

// Controller class for managing users
@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // CREATE
    @PostMapping
    public ResponseEntity<UtilisateurDTO> createUtilisateur(@Valid @RequestBody UtilisateurDTO dto) {
        try {
            // Validation des champs requis pour la création
            if (dto.getNom() == null || dto.getNom().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (dto.getPrenom() == null || dto.getPrenom().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (dto.getEmail() == null || dto.getEmail().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Utilisateur utilisateur = new Utilisateur(dto.getNom(), dto.getPrenom(), dto.getEmail());
            utilisateur.setTitreProfessionnel(dto.getTitreProfessionnel());
            utilisateur.setResume(dto.getResume());
            utilisateur.setVisibiliteProfil(dto.getVisibiliteProfil());
            Utilisateur created = utilisateurService.createUtilisateur(utilisateur);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // READ - Get all
    @GetMapping
    public ResponseEntity<List<UtilisateurDTO>> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();
        List<UtilisateurDTO> dtos = utilisateurs.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // READ - Get all visible
    @GetMapping("/visible")
    public ResponseEntity<List<UtilisateurDTO>> getAllVisibleProfiles() {
        List<Utilisateur> utilisateurs = utilisateurService.getAllVisibleProfiles();
        List<UtilisateurDTO> dtos = utilisateurs.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // READ - Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> getUtilisateurById(@PathVariable Integer id) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findByIdWithSkills(id);
        return utilisateur.map(u -> ResponseEntity.ok(toDTO(u)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // READ - Get by Email
    @GetMapping("/email/{email}")
    public ResponseEntity<UtilisateurDTO> getUtilisateurByEmail(@PathVariable String email) {
        Optional<Utilisateur> utilisateur = utilisateurService.getUtilisateurByEmail(email);
        return utilisateur.map(u -> ResponseEntity.ok(toDTO(u)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // READ - Search
    @GetMapping("/search")
    public ResponseEntity<List<UtilisateurDTO>> search(@RequestParam String query) {
        List<Utilisateur> utilisateurs = utilisateurService.searchByNomOrPrenom(query);
        List<UtilisateurDTO> dtos = utilisateurs.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // READ - Find by name
    @GetMapping("/nom/{nom}")
    public ResponseEntity<List<UtilisateurDTO>> findByNom(@PathVariable String nom) {
        List<Utilisateur> utilisateurs = utilisateurService.findByNom(nom);
        List<UtilisateurDTO> dtos = utilisateurs.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // READ - Find by first name
    @GetMapping("/prenom/{prenom}")
    public ResponseEntity<List<UtilisateurDTO>> findByPrenom(@PathVariable String prenom) {
        List<Utilisateur> utilisateurs = utilisateurService.findByPrenom(prenom);
        List<UtilisateurDTO> dtos = utilisateurs.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // READ - Find by skill
    @GetMapping("/skill/{skillId}")
    public ResponseEntity<List<UtilisateurDTO>> findBySkill(@PathVariable Integer skillId) {
        List<Utilisateur> utilisateurs = utilisateurService.findBySkill(skillId);
        List<UtilisateurDTO> dtos = utilisateurs.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // READ - Find by min skills
    @GetMapping("/minSkills/{minSkills}")
    public ResponseEntity<List<UtilisateurDTO>> findByMinSkills(@PathVariable int minSkills) {
        List<Utilisateur> utilisateurs = utilisateurService.findByMinSkills(minSkills);
        List<UtilisateurDTO> dtos = utilisateurs.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // READ - Find by min connections
    @GetMapping("/minConnections/{minConnections}")
    public ResponseEntity<List<UtilisateurDTO>> findByMinConnections(@PathVariable int minConnections) {
        List<Utilisateur> utilisateurs = utilisateurService.findByMinConnections(minConnections);
        List<UtilisateurDTO> dtos = utilisateurs.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> updateUtilisateur(@PathVariable Integer id, @RequestBody UtilisateurDTO dto) {
        try {
            Utilisateur details = new Utilisateur();
            details.setIdUtilisateur(id);
            
            // Mise à jour partielle - ne mettre à jour que les champs fournis
            if (dto.getNom() != null && !dto.getNom().isBlank()) {
                details.setNom(dto.getNom());
            }
            if (dto.getPrenom() != null && !dto.getPrenom().isBlank()) {
                details.setPrenom(dto.getPrenom());
            }
            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                details.setEmail(dto.getEmail());
            }
            if (dto.getTitreProfessionnel() != null && !dto.getTitreProfessionnel().isBlank()) {
                details.setTitreProfessionnel(dto.getTitreProfessionnel());
            }
            if (dto.getResume() != null && !dto.getResume().isBlank()) {
                details.setResume(dto.getResume());
            }
            if (dto.getVisibiliteProfil() != null) {
                details.setVisibiliteProfil(dto.getVisibiliteProfil());
            }

            Utilisateur updated = utilisateurService.updateUtilisateur(id, details);
            return ResponseEntity.ok(toDTO(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // UPDATE - Visibility
    @PutMapping("/{id}/visibility")
    public ResponseEntity<UtilisateurDTO> updateVisibility(@PathVariable Integer id, @RequestParam Boolean visible) {
        try {
            Utilisateur updated = utilisateurService.updateVisibility(id, visible);
            return ResponseEntity.ok(toDTO(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // UPDATE - Skills
    @PutMapping("/{id}/skills")
    @CrossOrigin(origins = "*")
    public ResponseEntity<UtilisateurDTO> updateSkills(@PathVariable Integer id, @RequestBody List<Integer> skillIds) {
        try {
            java.util.Set<Skill> skills = new java.util.HashSet<>();
            for (Integer skillId : skillIds) {
                skillService.getSkillById(skillId).ifPresent(skills::add);
            }
            Utilisateur updated = utilisateurService.updateSkills(id, skills);
            return ResponseEntity.ok(toDTO(updated));
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

    // Mapper helper
    private UtilisateurDTO toDTO(Utilisateur u) {
        if (u == null) return null;
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(u.getIdUtilisateur());
        dto.setEmail(u.getEmail());
        dto.setNom(u.getNom());
        dto.setPrenom(u.getPrenom());
        dto.setTitreProfessionnel(u.getTitreProfessionnel());
        dto.setResume(u.getResume());
        dto.setVisibiliteProfil(u.getVisibiliteProfil());
        if (u.getSkills() != null) {
            java.util.List<SkillDTO> skills = u.getSkills().stream()
                    .map(s -> new SkillDTO(s.getIdSkill(), s.getLibelle()))
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }
}
