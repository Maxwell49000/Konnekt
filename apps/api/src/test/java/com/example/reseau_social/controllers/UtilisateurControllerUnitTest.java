package com.example.reseau_social.controllers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.reseau_social.dtos.UtilisateurDTO;
import com.example.reseau_social.models.Utilisateur;
import com.example.reseau_social.services.SkillService;
import com.example.reseau_social.services.UtilisateurService;

@ExtendWith(MockitoExtension.class)
public class UtilisateurControllerUnitTest {

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private UtilisateurController controller;

    @Test
    public void createUtilisateur_returnsCreated() {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setNom("N"); dto.setPrenom("P"); dto.setEmail("e@e.com");

        Utilisateur created = new Utilisateur("N","P","e@e.com");
        created.setIdUtilisateur(1);

        when(utilisateurService.createUtilisateur(any())).thenReturn(created);

        var resp = controller.createUtilisateur(dto);
        assertEquals(201, resp.getStatusCode().value());
    }

    @Test
    public void getAllUtilisateurs_returnsOk() {
        when(utilisateurService.getAllUtilisateurs()).thenReturn(Arrays.asList(new Utilisateur()));

        var resp = controller.getAllUtilisateurs();
        assertEquals(200, resp.getStatusCode().value());
        List<UtilisateurDTO> body = resp.getBody();
        assertNotNull(body);
    }
}
