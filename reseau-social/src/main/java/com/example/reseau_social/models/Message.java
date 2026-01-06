package com.example.reseau_social.models;

import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Field("sender_id")
    private Integer senderId;
    private String text;
    @Field("created_at")
    private Instant createdAt;
}
