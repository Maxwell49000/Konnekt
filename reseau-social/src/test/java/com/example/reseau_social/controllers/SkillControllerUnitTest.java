package com.example.reseau_social.controllers;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.reseau_social.models.Skill;
import com.example.reseau_social.services.SkillService;

@ExtendWith(MockitoExtension.class)
public class SkillControllerUnitTest {

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillController controller;

    @Test
    public void getAll_returnsOk() {
        when(skillService.getAllSkills()).thenReturn(Arrays.asList(new Skill()));

        var resp = controller.getAllSkills();
        assertEquals(200, resp.getStatusCode().value());
    }
}
