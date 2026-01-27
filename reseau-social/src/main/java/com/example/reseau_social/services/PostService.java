package com.example.reseau_social.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reseau_social.dtos.CommentDTO;
import com.example.reseau_social.dtos.PostDTO;
import com.example.reseau_social.models.Comment;
import com.example.reseau_social.models.Notification;
import com.example.reseau_social.models.Post;
import com.example.reseau_social.models.Utilisateur;
import com.example.reseau_social.repositories.PostRepository;
import com.example.reseau_social.repositories.UtilisateurRepository;

import jakarta.transaction.Transactional;

// Service class for managing Post entities
@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private NotificationService notificationService;

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }

    public List<Post> getPostsByAuthor(Integer authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    public Post updatePost(String id, Post details) {
        return postRepository.findById(id).map(p -> {
            if (details.getText() != null) {
                p.setText(details.getText());
            }
            if (details.getMedia() != null) {
                p.setMedia(details.getMedia());
            }
            if (details.getVisibility() != null) {
                p.setVisibility(details.getVisibility());
            }
            if (details.getLikes() != null) {
                p.setLikes(details.getLikes());
            }
            return postRepository.save(p);
        }).orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));
    }

    public void deletePost(String id) {
        postRepository.deleteById(id);
    }
    public Post addComment(String postId, Comment comment) {
        return postRepository.findById(postId).map(p -> {
            p.getComments().add(comment);
            Post saved = postRepository.save(p);
            
            // Create notification for the post author (if not the comment author)
            if (!p.getAuthorId().equals(comment.getUserId())) {
                Optional<Utilisateur> commenterOpt = utilisateurRepository.findById(comment.getUserId());
                if (commenterOpt.isPresent()) {
                    Utilisateur commenter = commenterOpt.get();
                    Notification notification = new Notification();
                    notification.setUserId(p.getAuthorId());
                    notification.setType("comment");
                    notification.setContent(commenter.getPrenom() + " " + commenter.getNom() + " a commenté votre post");
                    notification.setRead(false);
                    notification.setCreatedAt(Instant.now());
                    notificationService.createNotification(notification);
                }
            }
            
            return saved;
        }).orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
    }

    public Post updateComment(String postId, String commentId, String newText) {
        return postRepository.findById(postId).map(p -> {
            Comment comment = p.getComments().stream()
                    .filter(c -> c.getId().equals(commentId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));
            comment.setText(newText);
            return postRepository.save(p);
        }).orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
    }

    public Post removeComment(String postId, String commentId) {
        return postRepository.findById(postId).map(p -> {
            boolean removed = p.getComments().removeIf(c -> c.getId().equals(commentId));
            if (!removed) {
                throw new IllegalArgumentException("Comment not found: " + commentId);
            }
            return postRepository.save(p);
        }).orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
    }

    public Post addLike(String postId, Integer userId) {
        return postRepository.findById(postId).map(p -> {
            if (p.getLikes() == null) {
                p.setLikes(new java.util.ArrayList<>());
            }
            if (!p.getLikes().contains(userId)) {
                p.getLikes().add(userId);
                Post saved = postRepository.save(p);
                
                // Create notification for the post author (if not the liker)
                if (!p.getAuthorId().equals(userId)) {
                    Optional<Utilisateur> likerOpt = utilisateurRepository.findById(userId);
                    if (likerOpt.isPresent()) {
                        Utilisateur liker = likerOpt.get();
                        Notification notification = new Notification();
                        notification.setUserId(p.getAuthorId());
                        notification.setType("like");
                        notification.setContent(liker.getPrenom() + " " + liker.getNom() + " a aimé votre post");
                        notification.setRead(false);
                        notification.setCreatedAt(Instant.now());
                        notificationService.createNotification(notification);
                    }
                }
                
                return saved;
            }
            return postRepository.save(p);
        }).orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
    }

    public Post removeLike(String postId, Integer userId) {
        return postRepository.findById(postId).map(p -> {
            if (p.getLikes() != null) {
                p.getLikes().removeIf(id -> id.equals(userId));
            }
            return postRepository.save(p);
        }).orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
    }

    // Mappers
    public Post createPostFromDTO(PostDTO dto) {
        Post post = new Post();
        post.setText(dto.getContenu());
        post.setAuthorId(dto.getAuteurId());
        post.setCreatedAt(Instant.now());
        post.setComments(new java.util.ArrayList<>());
        post.setLikes(new java.util.ArrayList<>());
        return post;
    }

    public PostDTO postToResponseDTO(Post post) {
        List<CommentDTO> commentDTOs = post.getComments().stream()
                .map(c -> new CommentDTO(c.getId(), c.getText(), c.getUserId(), c.getCreatedAt()))
                .collect(Collectors.toList());
        
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setContenu(post.getText());
        dto.setAuteurId(post.getAuthorId());
        dto.setLikes(post.getLikes() != null ? post.getLikes() : new java.util.ArrayList<>());
        dto.setDateCreation(post.getCreatedAt());
        dto.setComments(commentDTOs);
        return dto;
    }

    public List<PostDTO> postsToResponseDTOList(List<Post> posts) {
        return posts.stream().map(this::postToResponseDTO).collect(Collectors.toList());
    }
}
