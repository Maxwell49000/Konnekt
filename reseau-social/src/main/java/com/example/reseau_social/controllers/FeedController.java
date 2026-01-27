package com.example.reseau_social.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.example.reseau_social.dtos.CommentDTO;
import com.example.reseau_social.dtos.PostDTO;
import com.example.reseau_social.models.Feed;
import com.example.reseau_social.services.FeedService;
import com.example.reseau_social.services.PostService;

@RestController
@RequestMapping("/api/feeds")
@CrossOrigin(origins = "*")
public class FeedController {

    @Autowired
    private FeedService feedService;

    @Autowired
    private PostService postService;

    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @PostMapping
    public ResponseEntity<Feed> create(@RequestBody Feed feed) {
        Feed created = feedService.createFeed(feed);
        return ResponseEntity.created(URI.create("/api/feeds/" + created.getId())).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Feed>> list() {
        return ResponseEntity.ok(feedService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feed> get(@PathVariable String id) {
        Optional<Feed> f = feedService.getById(id);
        return f.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Feed>> byUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(feedService.findByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feed> update(@PathVariable String id, @RequestBody Feed details) {
        try {
            Feed updated = feedService.updateFeed(id, details);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        feedService.deleteFeed(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/posts")
    public ResponseEntity<List<PostDTO>> postsByUser(@PathVariable Integer userId) {
        List<Feed> feeds = feedService.findByUser(userId);
        logger.debug("Found {} feed(s) for user {}", feeds.size(), userId);

        List<String> postIds = feeds.stream()
            .flatMap(f -> f.getPosts() == null ? java.util.stream.Stream.empty() : f.getPosts().stream())
            .map(Object::toString)
            .collect(Collectors.toList());

        logger.debug("Aggregated postIds from feeds: {}", postIds);

        List<PostDTO> posts = postIds.stream()
            .map(id -> postService.getPostById(id))
            .filter(Optional::isPresent)
            .map(opt -> toDTO(opt.get()))
            .collect(Collectors.toList());

        if (posts.isEmpty()) {
            logger.debug("No posts found from feeds for user {} — falling back to all posts", userId);
            return ResponseEntity.ok(postService.getAllPosts().stream().map(this::toDTO).collect(Collectors.toList()));
        }

        return ResponseEntity.ok(posts);
    }

    private PostDTO toDTO(com.example.reseau_social.models.Post post) {
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
}
