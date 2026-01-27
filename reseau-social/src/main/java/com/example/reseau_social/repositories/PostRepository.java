package com.example.reseau_social.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.reseau_social.models.Post;

import java.util.List;

// Repository interface for Post model
@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByAuthorId(Integer authorId);
}
