package com.example.reseau_social.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    private String id;

    @Field("author_id")
    private Integer authorId;
    private String text;
    private List<String> media = new ArrayList<>();
    private String visibility;
    @Field("created_at")
    private Instant createdAt;
    private List<Comment> comments = new ArrayList<>();
    private List<Integer> likes = new ArrayList<>();
}
