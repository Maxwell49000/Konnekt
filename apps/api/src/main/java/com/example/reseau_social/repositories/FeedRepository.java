package com.example.reseau_social.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.reseau_social.models.Feed;

import java.util.List;

//  Repository interface for Feed model
@Repository
public interface FeedRepository extends MongoRepository<Feed, String> {
    List<Feed> findByUserId(Integer userId);
}
