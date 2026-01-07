package com.example.reseau_social.models;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private String id = UUID.randomUUID().toString();
    @Field("user_id")
    private Integer userId;
    private String text;
    @Field("created_at")
    private Instant createdAt;
}
