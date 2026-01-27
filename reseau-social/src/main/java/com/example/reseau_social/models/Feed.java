package com.example.reseau_social.models;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Model for user feeds
@Document(collection = "feeds")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feed {
    @Id
    private String id;

    private Integer userId;
    private List<Integer> posts;
    private Instant updatedAt;
}
