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

import com.example.reseau_social.dtos.CreateSkillDTO;
import com.example.reseau_social.models.Skill;
import com.example.reseau_social.services.SkillService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = "*")
public class SkillController {

    @Autowired
    private SkillService skillService;

    // CREATE
    @PostMapping
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody CreateSkillDTO dto) {
        try {
            Skill skill = new Skill(dto.getLibelle());
            Skill created = skillService.createSkill(skill);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // READ - Get all
    @GetMapping
    public ResponseEntity<List<Skill>> getAllSkills() {
        List<Skill> skills = skillService.getAllSkills();
        return ResponseEntity.ok(skills);
    }

    // READ - Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<Skill> getSkillById(@PathVariable Integer id) {
        Optional<Skill> skill = skillService.getSkillById(id);
        return skill.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // READ - Get by libelle
    @GetMapping("/libelle/{libelle}")
    public ResponseEntity<Skill> getSkillByLibelle(@PathVariable String libelle) {
        Optional<Skill> skill = skillService.getSkillByLibelle(libelle);
        return skill.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // READ - Search
    @GetMapping("/search")
    public ResponseEntity<List<Skill>> search(@RequestParam String query) {
        List<Skill> skills = skillService.findByLibelle(query);
        return ResponseEntity.ok(skills);
    }

    // READ - Most used skills
    @GetMapping("/mostUsed")
    public ResponseEntity<List<Skill>> getMostUsedSkills() {
        List<Skill> skills = skillService.getMostUsedSkills();
        return ResponseEntity.ok(skills);
    }

    // READ - Find by min utilisateurs
    @GetMapping("/minUtilisateurs/{minCount}")
    public ResponseEntity<List<Skill>> findSkillsWithMinUtilisateurs(@PathVariable int minCount) {
        List<Skill> skills = skillService.findSkillsWithMinUtilisateurs(minCount);
        return ResponseEntity.ok(skills);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Skill> updateSkill(@PathVariable Integer id, @RequestBody Skill skillDetails) {
        try {
            Skill updated = skillService.updateSkill(id, skillDetails);
            return ResponseEntity.ok(updated);
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
}
