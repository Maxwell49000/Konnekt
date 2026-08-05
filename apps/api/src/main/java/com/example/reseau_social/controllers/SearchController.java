package com.example.reseau_social.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.reseau_social.dtos.SearchResultDTO;
import com.example.reseau_social.services.SearchService;

import lombok.RequiredArgsConstructor;

// Controller class for managing search functionality
@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class SearchController {
    
    private final SearchService searchService;
    
    /**
     * Recherche avancée globale avec filtrage par catégorie
     * @param query Terme de recherche
     * @param categories Catégories à chercher: utilisateurs, skills, experiences (optionnel)
     * @return Résultats groupés par catégorie
     */
    @GetMapping("/advanced")
    public ResponseEntity<SearchResultDTO> advancedSearch(
            @RequestParam String query,
            @RequestParam(required = false) List<String> categories) {
        
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        SearchResultDTO results = searchService.advancedSearch(query, categories);
        return ResponseEntity.ok(results);
    }
}
