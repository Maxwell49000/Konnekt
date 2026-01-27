package com.example.reseau_social.controllers;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.reseau_social.dtos.CommentDTO;
import com.example.reseau_social.dtos.PostDTO;
import com.example.reseau_social.models.Comment;
import com.example.reseau_social.models.Post;
import com.example.reseau_social.services.PostService;

import jakarta.validation.Valid;

// Controller class for managing posts
@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * Create a new Post from the provided DTO and return the created resource.
     *
     * Flow / responsabilités :
     * - Validation automatique du DTO : `@Valid` (Jakarta Validation) applique les contraintes
     *   définies dans `CreatePostDTO` avant d'entrer dans la méthode.
     * - Conversion DTO -> Entity : `postService.createPostFromDTO(dto)` (dans
     *   `com.example.reseau_social.services.PostService`) construit un `Post` à partir du DTO.
     * - Persistance : `postService.createPost(post)` persiste l'entité (en déléguant au
     *   `Repository` interne, ex. `postRepository.save(...)`) et renvoie l'entité sauvegardée
     *   (avec id/timestamps).
     * - Conversion Entity -> Response DTO : `postService.postToResponseDTO(created)` construit
     *   le `PostResponseDTO` renvoyé au client (masque champs sensibles, formate la sortie).
     *
     * Comportement HTTP :
     * - Exposé via `@PostMapping` sur `/api/posts` (la route de classe est définie par
     *   `@RequestMapping("/api/posts")`).
     * - Retourne `201 Created` avec header `Location: /api/posts/{id}` et le `PostResponseDTO`
     *   en body.
     *
     * Exceptions attendues / gestion des erreurs :
     * - Les violations de validation (constraint violations) sont gérées par Spring et
     *   retournent typiquement un 400. Si tu veux personnaliser, utiliser un `@ControllerAdvice`.
     * - Les erreurs métier (ex. auteur introuvable) peuvent lancer `IllegalArgumentException`
     *   ou une exception spécifique depuis `PostService` : il est préférable de les mapper
     *   proprement via un `@ControllerAdvice` pour renvoyer 404/400 selon le cas.
     */
    @PostMapping
    public ResponseEntity<PostDTO> create(@Valid @RequestBody PostDTO dto) {
        Post post = new Post();
        post.setText(dto.getContenu());
        post.setAuthorId(dto.getAuteurId());
        post.setCreatedAt(Instant.now());
        post.setComments(new java.util.ArrayList<>());
        post.setLikes(new java.util.ArrayList<>());

        Post created = postService.createPost(post);
        PostDTO response = toDTO(created);

        return ResponseEntity.created(URI.create("/api/posts/" + created.getId())).body(response);
    }

    // List all posts
    @GetMapping
    public ResponseEntity<List<PostDTO>> list() {
        List<Post> posts = postService.getAllPosts();
        List<PostDTO> responses = posts.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(responses);
    }

    // Get a specific post by ID
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> get(@PathVariable String id) {
        Optional<Post> p = postService.getPostById(id);
        return p.map(post -> ResponseEntity.ok(toDTO(post)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get posts by a specific author
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<PostDTO>> byAuthor(@PathVariable Integer authorId) {
        List<Post> posts = postService.getPostsByAuthor(authorId);
        List<PostDTO> responses = posts.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(responses);
    }

    // Update an existing post
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> update(@PathVariable String id, @RequestBody PostDTO dto) {
        try {
            Post details = new Post();
            details.setText(dto.getContenu());
            details.setMedia(dto.getMedia());
            details.setVisibility(dto.getVisibility());
            details.setLikes(dto.getLikes());

            Post updated = postService.updatePost(id, details);
            PostDTO response = toDTO(updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Delete a post by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // Add a comment to a post
    @PostMapping("/{id}/comments")
    public ResponseEntity<PostDTO> addComment(@PathVariable String id, @RequestBody CommentDTO dto) {
        try {
            Comment comment = new Comment();
            comment.setId(java.util.UUID.randomUUID().toString());
            comment.setText(dto.getContenu());
            comment.setUserId(dto.getAuteurId());
            comment.setCreatedAt(Instant.now());
            
            Post result = postService.addComment(id, comment);
            PostDTO response = toDTO(result);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Update a comment on a post
    @PutMapping("/{id}/comments/{commentId}")
    public ResponseEntity<PostDTO> updateComment(@PathVariable String id, @PathVariable String commentId, @RequestBody CommentDTO dto) {
        try {
            Post result = postService.updateComment(id, commentId, dto.getContenu());
            PostDTO response = toDTO(result);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Delete a comment from a post
    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<PostDTO> deleteComment(@PathVariable String id, @PathVariable String commentId) {
        try {
            Post result = postService.removeComment(id, commentId);
            PostDTO response = toDTO(result);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Like a post
    @PostMapping("/{id}/like")
    public ResponseEntity<PostDTO> like(@PathVariable String id, @RequestParam Integer userId) {
        try {
            Post updated = postService.addLike(id, userId);
            PostDTO response = toDTO(updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Unlike a post
    @PostMapping("/{id}/unlike")
    public ResponseEntity<PostDTO> unlike(@PathVariable String id, @RequestParam Integer userId) {
        try {
            Post updated = postService.removeLike(id, userId);
            PostDTO response = toDTO(updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Helper mapper
    private PostDTO toDTO(Post post) {
        List<CommentDTO> commentDTOs = post.getComments().stream()
                .map(c -> new CommentDTO(c.getId(), c.getText(), c.getUserId(), c.getCreatedAt()))
                .collect(java.util.stream.Collectors.toList());
        
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setContenu(post.getText());
        dto.setAuteurId(post.getAuthorId());
        dto.setLikes(post.getLikes() != null ? post.getLikes() : new java.util.ArrayList<>());
        dto.setDateCreation(post.getCreatedAt());
        dto.setComments(commentDTOs);
        return dto;
    }
}