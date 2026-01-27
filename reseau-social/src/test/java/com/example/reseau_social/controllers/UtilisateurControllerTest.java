package com.example.reseau_social.controllers;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.example.reseau_social.services.UtilisateurService;

public class UtilisateurControllerTest {

    @Mock
    private UtilisateurService utilisateurService;

    private UtilisateurController utilisateurController;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        utilisateurController = new UtilisateurController();
        // inject mock into private field
        Field serviceField = UtilisateurController.class.getDeclaredField("utilisateurService");
        serviceField.setAccessible(true);
        serviceField.set(utilisateurController, utilisateurService);
    }

    @Test
    public void testSearch_returnsOkAndEmptyList() throws Exception {
        when(utilisateurService.searchByNomOrPrenom("john")).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = utilisateurController.search("john");
        assertEquals(200, response.getStatusCode().value());
        assertEquals(0, ((java.util.List)response.getBody()).size());
    }
}
