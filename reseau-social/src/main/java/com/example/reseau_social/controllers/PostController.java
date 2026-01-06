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
import org.springframework.web.bind.annotation.RestController;

import com.example.reseau_social.models.Comment;
import com.example.reseau_social.models.Post;
import com.example.reseau_social.services.PostService;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<Post> create(@RequestBody Post post) {
        post.setCreatedAt(post.getCreatedAt() == null ? Instant.now() : post.getCreatedAt());
        Post created = postService.createPost(post);
        return ResponseEntity.created(URI.create("/api/posts/" + created.getId())).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Post>> list() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> get(@PathVariable String id) {
        Optional<Post> p = postService.getPostById(id);
        return p.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Post>> byAuthor(@PathVariable Integer authorId) {
        return ResponseEntity.ok(postService.getPostsByAuthor(authorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable String id, @RequestBody Post details) {
        try {
            Post updated = postService.updatePost(id, details);
            return ResponseEntity.ok(updated);
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
    public ResponseEntity<Post> addComment(@PathVariable String id, @RequestBody Comment comment) {
        comment.setCreatedAt(comment.getCreatedAt() == null ? Instant.now() : comment.getCreatedAt());
        try {
            Post result = postService.addComment(id, comment);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
