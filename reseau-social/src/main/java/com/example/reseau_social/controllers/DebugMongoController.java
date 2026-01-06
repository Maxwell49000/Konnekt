package com.example.reseau_social.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug/mongo")
public class DebugMongoController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private Environment env;

    @GetMapping("/collections")
    public ResponseEntity<Set<String>> listCollections() {
        return ResponseEntity.ok(mongoTemplate.getCollectionNames());
    }

    @GetMapping("/count/{collection}")
    public ResponseEntity<Map<String, Object>> countCollection(@PathVariable String collection) {
        Map<String, Object> resp = new HashMap<>();
        long count = mongoTemplate.getCollection(collection).countDocuments();
        resp.put("collection", collection);
        resp.put("count", count);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> resp = new HashMap<>();
        String dbName = mongoTemplate.getDb().getName();
        resp.put("dbName", dbName);
        String uri = env.getProperty("spring.data.mongodb.uri");
        resp.put("spring.data.mongodb.uri", uri);
        return ResponseEntity.ok(resp);
    }
}
