package com.example.reseau_social.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.example.reseau_social.dtos.SkillDTO;
import com.example.reseau_social.models.Skill;

public class SkillMapperTest {

    @Test
    public void toDto_mapsFields() {
        Skill s = new Skill("Java");
        s.setIdSkill(99);

        SkillDTO dto = new SkillDTO(s.getIdSkill(), s.getLibelle());

        assertNotNull(dto);
        assertEquals(99, dto.getId());
        assertEquals("Java", dto.getLibelle());
    }
}
