package com.example.reseau_social.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reseau_social.models.Post;
import com.example.reseau_social.models.Comment;
import com.example.reseau_social.repositories.PostRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }

    public List<Post> getPostsByAuthor(Integer authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    public Post updatePost(String id, Post details) {
        return postRepository.findById(id).map(p -> {
            p.setText(details.getText());
            p.setMedia(details.getMedia());
            p.setVisibility(details.getVisibility());
            p.setLikes(details.getLikes());
            return postRepository.save(p);
        }).orElseThrow(() -> new IllegalArgumentException("Post not found: " + id));
    }

    public void deletePost(String id) {
        postRepository.deleteById(id);
    }

    public Post addComment(String postId, Comment comment) {
        return postRepository.findById(postId).map(p -> {
            p.getComments().add(comment);
            return postRepository.save(p);
        }).orElseThrow(() -> new IllegalArgumentException("Post not found: " + postId));
    }
}
