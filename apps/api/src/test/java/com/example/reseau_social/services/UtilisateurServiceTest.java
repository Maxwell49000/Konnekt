package com.example.reseau_social.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.reseau_social.models.Utilisateur;
import com.example.reseau_social.repositories.UtilisateurRepository;

public class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private UtilisateurService utilisateurService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createUtilisateur_whenEmailExists_shouldThrow() {
        Utilisateur u = new Utilisateur();
        u.setEmail("test@example.com");
        when(utilisateurRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> utilisateurService.createUtilisateur(u));
    }

    @Test
    public void emailExists_shouldReturnTrueFalse() {
        when(utilisateurRepository.existsByEmail("a@a.com")).thenReturn(true);
        when(utilisateurRepository.existsByEmail("b@b.com")).thenReturn(false);

        assertEquals(true, utilisateurService.emailExists("a@a.com"));
        assertEquals(false, utilisateurService.emailExists("b@b.com"));
    }

    @Test
    public void countAllUtilisateurs_shouldReturnCount() {
        when(utilisateurRepository.count()).thenReturn(42L);
        assertEquals(42L, utilisateurService.countAllUtilisateurs());
    }
}
