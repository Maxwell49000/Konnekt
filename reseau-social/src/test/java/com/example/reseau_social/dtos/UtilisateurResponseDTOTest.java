package com.example.reseau_social.dtos;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class UtilisateurResponseDTOTest {

    @Test
    public void getters_and_setters() {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setIdUtilisateur(42);
        dto.setNom("Nom");
        dto.setPrenom("Prenom");
        dto.setEmail("a@b.com");

        assertEquals(42, dto.getIdUtilisateur());
        assertEquals(Integer.valueOf(42), dto.getId());
        assertEquals("Nom", dto.getNom());
        assertEquals("Prenom", dto.getPrenom());
        assertEquals("a@b.com", dto.getEmail());
    }

    @Test
    public void basicMapping_shouldHold() {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(10);
        dto.setNom("Nom");
        dto.setPrenom("Prenom");
        dto.setEmail("a@b.com");
        List<SkillDTO> skills = new ArrayList<>();
        skills.add(new SkillDTO(1, "Java"));
        dto.setSkills(skills);

        assertEquals(10, dto.getId());
        assertEquals("Nom", dto.getNom());
        assertEquals(1, dto.getSkills().size());
    }
}
