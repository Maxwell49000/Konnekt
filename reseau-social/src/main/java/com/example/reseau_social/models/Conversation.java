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

@Document(collection = "conversations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    @Id
    private String id;

    private List<Integer> participants = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    @Field("created_at")
    private Instant createdAt;
}
