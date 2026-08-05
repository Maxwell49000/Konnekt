package com.example.reseau_social.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reseau_social.models.Feed;
import com.example.reseau_social.repositories.FeedRepository;

import jakarta.transaction.Transactional;

// Service class for managing Feed entities
@Service
@Transactional
public class FeedService {

    @Autowired
    private FeedRepository feedRepository;

    public Feed createFeed(Feed feed) {
        return feedRepository.save(feed);
    }

    public List<Feed> getAll() {
        return feedRepository.findAll();
    }

    public Optional<Feed> getById(String id) {
        return feedRepository.findById(id);
    }

    public List<Feed> findByUser(Integer userId) {
        return feedRepository.findByUserId(userId);
    }

    public Feed updateFeed(String id, Feed details) {
        return feedRepository.findById(id).map(f -> {
            f.setPosts(details.getPosts());
            f.setUpdatedAt(details.getUpdatedAt());
            return feedRepository.save(f);
        }).orElseThrow(() -> new IllegalArgumentException("Feed not found: " + id));
    }

    public void deleteFeed(String id) {
        feedRepository.deleteById(id);
    }
}
