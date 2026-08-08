package com.example.reseau_social.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import com.example.reseau_social.models.Post;
import com.example.reseau_social.models.Skill;
import com.example.reseau_social.models.Utilisateur;
import com.example.reseau_social.repositories.ExperienceRepository;
import com.example.reseau_social.repositories.PostRepository;
import com.example.reseau_social.repositories.SkillRepository;
import com.example.reseau_social.repositories.UtilisateurRepository;

@ExtendWith(MockitoExtension.class)
class DemoDataInitializerTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private ExperienceRepository experienceRepository;
    @Mock
    private PostRepository postRepository;

    private DemoDataInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new DemoDataInitializer(
                utilisateurRepository, skillRepository, experienceRepository, postRepository);

        AtomicInteger skillIds = new AtomicInteger(1);
        AtomicInteger userIds = new AtomicInteger(1);
        when(skillRepository.findByLibelle(any())).thenReturn(Optional.empty());
        when(skillRepository.save(any(Skill.class))).thenAnswer(invocation -> {
            Skill skill = invocation.getArgument(0);
            skill.setIdSkill(skillIds.getAndIncrement());
            return skill;
        });
        when(utilisateurRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(invocation -> {
            Utilisateur utilisateur = invocation.getArgument(0);
            utilisateur.setIdUtilisateur(userIds.getAndIncrement());
            return utilisateur;
        });
        when(experienceRepository.findByUtilisateurIdUtilisateur(any())).thenReturn(Collections.emptyList());
        when(postRepository.findByAuthorId(any())).thenReturn(Collections.emptyList());
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void runSeedsAUsableDemoWorkspace() throws Exception {
        initializer.run(mock(ApplicationArguments.class));

        verify(skillRepository, times(9)).save(any(Skill.class));
        verify(utilisateurRepository, times(3)).save(any(Utilisateur.class));
        verify(experienceRepository, times(4)).save(any());
        verify(postRepository, times(3)).save(any(Post.class));
    }
}
