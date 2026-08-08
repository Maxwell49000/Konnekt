package com.example.reseau_social.config;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.example.reseau_social.models.Experience;
import com.example.reseau_social.models.Post;
import com.example.reseau_social.models.Skill;
import com.example.reseau_social.models.Utilisateur;
import com.example.reseau_social.repositories.ExperienceRepository;
import com.example.reseau_social.repositories.PostRepository;
import com.example.reseau_social.repositories.SkillRepository;
import com.example.reseau_social.repositories.UtilisateurRepository;

import jakarta.transaction.Transactional;

@Component
@ConditionalOnProperty(name = "app.demo-data.enabled", havingValue = "true")
public class DemoDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);

    private final UtilisateurRepository utilisateurRepository;
    private final SkillRepository skillRepository;
    private final ExperienceRepository experienceRepository;
    private final PostRepository postRepository;

    public DemoDataInitializer(
            UtilisateurRepository utilisateurRepository,
            SkillRepository skillRepository,
            ExperienceRepository experienceRepository,
            PostRepository postRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.skillRepository = skillRepository;
        this.experienceRepository = experienceRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Skill designProduit = getOrCreateSkill("Design produit");
        Skill rechercheUtilisateur = getOrCreateSkill("Recherche utilisateur");
        Skill prototypage = getOrCreateSkill("Prototypage");
        Skill vue = getOrCreateSkill("Vue.js");
        Skill springBoot = getOrCreateSkill("Spring Boot");
        Skill api = getOrCreateSkill("Architecture API");
        Skill strategie = getOrCreateSkill("Stratégie produit");
        Skill facilitation = getOrCreateSkill("Facilitation");
        Skill pilotage = getOrCreateSkill("Pilotage de projet");

        Utilisateur camille = getOrCreateUser(
                "Martin", "Camille", "demo@konnekt.local", "Product designer",
                "Je conçois des produits numériques clairs, accessibles et utiles. Mon travail relie recherche utilisateur, stratégie produit et design d’interface.",
                Set.of(designProduit, rechercheUtilisateur, prototypage));
        Utilisateur lina = getOrCreateUser(
                "Bernard", "Lina", "lina@konnekt.local", "Développeuse full-stack",
                "Je transforme des idées produit en applications robustes, maintenables et agréables à utiliser.",
                Set.of(vue, springBoot, api));
        Utilisateur noah = getOrCreateUser(
                "Diallo", "Noah", "noah@konnekt.local", "Chef de projet digital",
                "J’accompagne les équipes de la stratégie produit à la mise en ligne, avec une attention particulière portée à la collaboration.",
                Set.of(strategie, facilitation, pilotage));

        ensureExperience(camille, "Product designer", "Studio Nébuleuse", LocalDate.of(2023, 9, 1), null,
                "Conception de produits B2B, recherche utilisateur et accompagnement des équipes produit.");
        ensureExperience(camille, "UX designer", "Atelier North", LocalDate.of(2021, 3, 1), LocalDate.of(2023, 8, 31),
                "Refonte de parcours complexes et création d’un design system partagé.");
        ensureExperience(lina, "Développeuse full-stack", "Collectif Horizon", LocalDate.of(2022, 6, 1), null,
                "Développement d’applications web et industrialisation de composants partagés.");
        ensureExperience(noah, "Chef de projet digital", "Maison Atlas", LocalDate.of(2022, 1, 10), null,
                "Pilotage de projets numériques et animation d’équipes pluridisciplinaires.");

        Instant now = Instant.now();
        ensurePost(camille,
                "Nous venons de terminer une série de tests utilisateurs sur notre nouveau parcours. Trois entretiens ont suffi à faire émerger une simplification décisive.",
                now.minus(5, ChronoUnit.HOURS), List.of(lina.getIdUtilisateur(), noah.getIdUtilisateur()));
        ensurePost(lina,
                "Je viens de documenter notre stratégie de composants frontend : conventions, accessibilité et critères de revue. Le résultat accélère déjà les échanges.",
                now.minus(3, ChronoUnit.HOURS), List.of(camille.getIdUtilisateur()));
        ensurePost(noah,
                "Retour sur notre atelier produit de la semaine : moins de fonctionnalités, des objectifs mieux définis et une équipe alignée sur la valeur.",
                now.minus(1, ChronoUnit.HOURS), List.of(camille.getIdUtilisateur(), lina.getIdUtilisateur()));

        log.info("Konnekt demo data is ready (login: demo@konnekt.local)");
    }

    private Skill getOrCreateSkill(String label) {
        return skillRepository.findByLibelle(label)
                .orElseGet(() -> skillRepository.save(new Skill(label)));
    }

    private Utilisateur getOrCreateUser(
            String nom,
            String prenom,
            String email,
            String titre,
            String resume,
            Set<Skill> skills) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseGet(() -> {
                    Utilisateur created = new Utilisateur(nom, prenom, email);
                    created.setTitreProfessionnel(titre);
                    created.setResume(resume);
                    created.setVisibiliteProfil(true);
                    return created;
                });

        if (utilisateur.getTitreProfessionnel() == null || utilisateur.getTitreProfessionnel().isBlank()) {
            utilisateur.setTitreProfessionnel(titre);
        }
        if (utilisateur.getResume() == null || utilisateur.getResume().isBlank()) {
            utilisateur.setResume(resume);
        }
        Set<Skill> mergedSkills = new HashSet<>(utilisateur.getSkills());
        mergedSkills.addAll(skills);
        utilisateur.setSkills(mergedSkills);
        return utilisateurRepository.save(utilisateur);
    }

    private void ensureExperience(
            Utilisateur utilisateur,
            String poste,
            String entreprise,
            LocalDate dateDebut,
            LocalDate dateFin,
            String description) {
        boolean exists = experienceRepository.findByUtilisateurIdUtilisateur(utilisateur.getIdUtilisateur()).stream()
                .anyMatch(exp -> poste.equals(exp.getPoste()) && entreprise.equals(exp.getEntreprise()));
        if (exists) {
            return;
        }

        Experience experience = new Experience(poste, entreprise, dateDebut);
        experience.setDateFin(dateFin);
        experience.setDescription(description);
        experience.setUtilisateur(utilisateur);
        experienceRepository.save(experience);
    }

    private void ensurePost(Utilisateur auteur, String contenu, Instant dateCreation, List<Integer> likes) {
        boolean exists = postRepository.findByAuthorId(auteur.getIdUtilisateur()).stream()
                .anyMatch(post -> contenu.equals(post.getText()));
        if (exists) {
            return;
        }

        Post post = new Post();
        post.setAuthorId(auteur.getIdUtilisateur());
        post.setText(contenu);
        post.setCreatedAt(dateCreation);
        post.setVisibility("PUBLIC");
        post.setMedia(new ArrayList<>());
        post.setComments(new ArrayList<>());
        post.setLikes(new ArrayList<>(likes));
        postRepository.save(post);
    }
}
