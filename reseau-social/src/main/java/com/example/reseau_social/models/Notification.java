package com.example.reseau_social.models;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    private String id;

    private Integer userId;
    private String type;
    private String content;
    private Integer relatedPostId;
    private Instant createdAt;
    private Boolean read;
}
