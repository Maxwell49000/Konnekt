package com.example.reseau_social.services;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.example.reseau_social.dtos.SearchResultDTO;
import com.example.reseau_social.repositories.ExperienceRepository;
import com.example.reseau_social.repositories.SkillRepository;
import com.example.reseau_social.repositories.UtilisateurRepository;

public class SearchServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ExperienceRepository experienceRepository;

    @InjectMocks
    private SearchService searchService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void advancedSearch_noResults_shouldReturnEmptyDTO() {
        when(utilisateurRepository.searchByNomOrPrenom("x")).thenReturn(Collections.emptyList());
        when(skillRepository.findByLibelleContainingIgnoreCase("x")).thenReturn(Collections.emptyList());
        when(experienceRepository.searchByEntrepriseOrPoste("x")).thenReturn(Collections.emptyList());

        SearchResultDTO result = searchService.advancedSearch("x", List.of());
        assertEquals(0, result.getTotalResults());
        assertEquals(0, result.getUtilisateurs().size());
        assertEquals(0, result.getSkills().size());
        assertEquals(0, result.getExperiences().size());
    }
}
