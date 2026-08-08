package com.example.reseau_social.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Optional;
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

    @Test
    public void updateUtilisateur_shouldReturnEntityWithInitializedSkills() {
        Utilisateur existing = new Utilisateur("Martin", "Camille", "demo@konnekt.local");
        existing.setIdUtilisateur(1);
        Utilisateur details = new Utilisateur();
        details.setTitreProfessionnel("Product designer senior");

        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(existing));
        when(utilisateurRepository.save(existing)).thenReturn(existing);
        when(utilisateurRepository.findByIdWithSkills(1)).thenReturn(Optional.of(existing));

        Utilisateur updated = utilisateurService.updateUtilisateur(1, details);

        assertEquals("Product designer senior", updated.getTitreProfessionnel());
    }
}
