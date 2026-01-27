package com.example.reseau_social.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResultDTO {
    private List<UtilisateurDTO> utilisateurs;
    private List<SkillDTO> skills;
    private List<ExperienceDTO> experiences;
    private int totalResults;
}
