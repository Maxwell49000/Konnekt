package com.example.reseau_social.controllers;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

import com.example.reseau_social.dtos.CreateCommentDTO;
import com.example.reseau_social.dtos.CreatePostDTO;
import com.example.reseau_social.dtos.PostResponseDTO;
import com.example.reseau_social.models.Comment;
import com.example.reseau_social.models.Post;
import com.example.reseau_social.services.PostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDTO> create(@Valid @RequestBody CreatePostDTO dto) {
        Post post = postService.createPostFromDTO(dto);
        Post created = postService.createPost(post);
        PostResponseDTO response = postService.postToResponseDTO(created);
        return ResponseEntity.created(URI.create("/api/posts/" + created.getId())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> list() {
        List<Post> posts = postService.getAllPosts();
        List<PostResponseDTO> responses = postService.postsToResponseDTOList(posts);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> get(@PathVariable String id) {
        Optional<Post> p = postService.getPostById(id);
        return p.map(post -> ResponseEntity.ok(postService.postToResponseDTO(post)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<PostResponseDTO>> byAuthor(@PathVariable Integer authorId) {
        List<Post> posts = postService.getPostsByAuthor(authorId);
        List<PostResponseDTO> responses = postService.postsToResponseDTOList(posts);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> update(@PathVariable String id, @RequestBody Post details) {
        try {
            Post updated = postService.updatePost(id, details);
            PostResponseDTO response = postService.postToResponseDTO(updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<PostResponseDTO> addComment(@PathVariable String id, @RequestBody CreateCommentDTO dto) {
        try {
            Comment comment = new Comment();
            comment.setId(UUID.randomUUID().toString());
            comment.setText(dto.getContenu());
            comment.setUserId(dto.getAuteurId());
            comment.setCreatedAt(Instant.now());
            
            Post result = postService.addComment(id, comment);
            PostResponseDTO response = postService.postToResponseDTO(result);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}/comments/{commentId}")
    public ResponseEntity<PostResponseDTO> updateComment(@PathVariable String id, @PathVariable String commentId, @RequestBody CreateCommentDTO dto) {
        try {
            Post result = postService.updateComment(id, commentId, dto.getContenu());
            PostResponseDTO response = postService.postToResponseDTO(result);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<PostResponseDTO> deleteComment(@PathVariable String id, @PathVariable String commentId) {
        try {
            Post result = postService.removeComment(id, commentId);
            PostResponseDTO response = postService.postToResponseDTO(result);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<PostResponseDTO> like(@PathVariable String id, @RequestParam Integer userId) {
        try {
            Post updated = postService.addLike(id, userId);
            PostResponseDTO response = postService.postToResponseDTO(updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<PostResponseDTO> unlike(@PathVariable String id, @RequestParam Integer userId) {
        try {
            Post updated = postService.removeLike(id, userId);
            PostResponseDTO response = postService.postToResponseDTO(updated);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
