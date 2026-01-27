package com.example.reseau_social.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.reseau_social.models.Comment;
import com.example.reseau_social.models.Notification;
import com.example.reseau_social.models.Post;
import com.example.reseau_social.models.Utilisateur;
import com.example.reseau_social.repositories.PostRepository;
import com.example.reseau_social.repositories.UtilisateurRepository;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PostService postService;

    @Test
    public void addComment_createsNotificationWhenAuthorDifferent() {
        Post post = new Post();
        post.setId("p1");
        post.setAuthorId(10);
        post.setComments(new ArrayList<>());

        Comment comment = new Comment();
        comment.setId("c1");
        comment.setUserId(20);
        comment.setText("Nice post");

        Utilisateur commenter = new Utilisateur();
        commenter.setIdUtilisateur(20);
        commenter.setPrenom("Jean");
        commenter.setNom("Dupont");

        when(postRepository.findById("p1")).thenReturn(Optional.of(post));
        when(utilisateurRepository.findById(20)).thenReturn(Optional.of(commenter));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        Post result = postService.addComment("p1", comment);

        assertNotNull(result);
        assertEquals(1, result.getComments().size());
        assertEquals("c1", result.getComments().get(0).getId());
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    public void addLike_createsNotificationWhenLikerDifferent() {
        Post post = new Post();
        post.setId("p2");
        post.setAuthorId(10);
        post.setLikes(new ArrayList<>());

        Utilisateur liker = new Utilisateur();
        liker.setIdUtilisateur(20);
        liker.setPrenom("Alice");
        liker.setNom("Martin");

        when(postRepository.findById("p2")).thenReturn(Optional.of(post));
        when(utilisateurRepository.findById(20)).thenReturn(Optional.of(liker));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        Post result = postService.addLike("p2", 20);

        assertNotNull(result);
        assertTrue(result.getLikes().contains(20));
        verify(notificationService, times(1)).createNotification(any(Notification.class));
    }

    @Test
    public void removeLike_removesUserFromLikes() {
        Post post = new Post();
        post.setId("p3");
        post.setLikes(new ArrayList<>(Arrays.asList(20, 30)));

        when(postRepository.findById("p3")).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        Post result = postService.removeLike("p3", 20);

        assertNotNull(result);
        assertFalse(result.getLikes().contains(20));
        assertTrue(result.getLikes().contains(30));
        verify(notificationService, never()).createNotification(any());
    }

    @Test
    public void updateComment_updatesText() {
        Comment c = new Comment();
        c.setId("cm1");
        c.setText("old");

        Post post = new Post();
        post.setId("p4");
        post.setComments(new ArrayList<>(Arrays.asList(c)));

        when(postRepository.findById("p4")).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        Post result = postService.updateComment("p4", "cm1", "new text");

        assertNotNull(result);
        assertEquals(1, result.getComments().size());
        assertEquals("new text", result.getComments().get(0).getText());
    }
}
