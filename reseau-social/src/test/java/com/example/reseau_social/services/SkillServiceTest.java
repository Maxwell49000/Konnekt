package com.example.reseau_social.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.reseau_social.models.Skill;
import com.example.reseau_social.repositories.SkillRepository;

public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService skillService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createSkill_whenExists_shouldThrow() {
        Skill s = new Skill();
        s.setLibelle("Java");
        when(skillRepository.existsByLibelle("Java")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> skillService.createSkill(s));
    }

    @Test
    public void skilleExists_and_countAllSkills() {
        when(skillRepository.existsByLibelle("JS")).thenReturn(true);
        when(skillRepository.count()).thenReturn(10L);

        assertEquals(true, skillService.skilleExists("JS"));
        assertEquals(10L, skillService.countAllSkills());
    }
}
