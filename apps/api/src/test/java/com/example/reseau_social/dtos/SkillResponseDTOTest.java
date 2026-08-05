package com.example.reseau_social.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class SkillResponseDTOTest {

    @Test
    public void gettersAndSetters_shouldWork() {
        SkillDTO dto = new SkillDTO(1, "Java");
        assertEquals(1, dto.getId());
        assertEquals("Java", dto.getLibelle());
    }
}
