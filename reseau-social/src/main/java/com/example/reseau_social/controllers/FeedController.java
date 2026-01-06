package com.example.reseau_social.controllers;

import java.net.URI;
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

import com.example.reseau_social.models.Feed;
import com.example.reseau_social.services.FeedService;

@RestController
@RequestMapping("/api/feeds")
@CrossOrigin(origins = "*")
public class FeedController {

    @Autowired
    private FeedService feedService;

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
}
