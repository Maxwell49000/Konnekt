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
import com.example.reseau_social.models.Skill;
import com.example.reseau_social.services.SkillService;

import jakarta.validation.Valid;

// Controller class for managing skills
@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = "*")
public class SkillController {

    @Autowired
    private SkillService skillService;

    // CREATE
    @PostMapping
    public ResponseEntity<SkillDTO> createSkill(@RequestBody SkillDTO dto) {
        try {
            Skill skill = new Skill(dto.getLibelle());
            Skill created = skillService.createSkill(skill);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // READ - Get all
    @GetMapping
    public ResponseEntity<List<SkillDTO>> getAllSkills() {
        List<Skill> skills = skillService.getAllSkills();
        List<SkillDTO> dtos = skills.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // READ - Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<SkillDTO> getSkillById(@PathVariable Integer id) {
        Optional<Skill> skill = skillService.getSkillById(id);
        return skill.map(s -> ResponseEntity.ok(toDTO(s)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // READ - Get by libelle
    @GetMapping("/by-libelle")
    public ResponseEntity<SkillDTO> getSkillByLibelle(@RequestParam String libelle) {
        Optional<Skill> skill = skillService.getSkillByLibelle(libelle);
        return skill.map(s -> ResponseEntity.ok(toDTO(s)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // READ - Search
    @GetMapping("/search")
    public ResponseEntity<List<SkillDTO>> search(@RequestParam String query) {
        List<Skill> skills = skillService.findByLibelle(query);
        List<SkillDTO> dtos = skills.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // READ - Most used skills
    @GetMapping("/mostUsed")
    public ResponseEntity<List<SkillDTO>> getMostUsedSkills() {
        List<Skill> skills = skillService.getMostUsedSkills();
        List<SkillDTO> dtos = skills.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // READ - Find by min utilisateurs
    @GetMapping("/minUtilisateurs/{minCount}")
    public ResponseEntity<List<SkillDTO>> findSkillsWithMinUtilisateurs(@PathVariable int minCount) {
        List<Skill> skills = skillService.findSkillsWithMinUtilisateurs(minCount);
        List<SkillDTO> dtos = skills.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<SkillDTO> updateSkill(@PathVariable Integer id, @Valid @RequestBody SkillDTO dto) {
        try {
            Skill details = new Skill();
            details.setLibelle(dto.getLibelle());
            Skill updated = skillService.updateSkill(id, details);
            return ResponseEntity.ok(toDTO(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Integer id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }

    // STATISTICS
    @GetMapping("/stats/countAll")
    public ResponseEntity<Long> countAllSkills() {
        long count = skillService.countAllSkills();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/check-libelle")
    public ResponseEntity<Boolean> checkSkillExists(@RequestParam String libelle) {
        boolean exists = skillService.skilleExists(libelle);
        return ResponseEntity.ok(exists);
    }

    // Mapper helper
    private SkillDTO toDTO(Skill s) {
        if (s == null) return null;
        return new SkillDTO(s.getIdSkill(), s.getLibelle());
    }
}