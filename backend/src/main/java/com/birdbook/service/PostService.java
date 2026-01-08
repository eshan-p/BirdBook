package com.birdbook.service;

import com.birdbook.models.Post;
import com.birdbook.repository.PostDAO;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostDAO sDAO;

    public PostService(PostDAO sDAO) {
        this.sDAO = sDAO;
    }

    // Just for testing Spring Boot, can be removed later
    public Optional<Post> getPostById(ObjectId id) {
        return sDAO.findById(id);
    }

    public List<Post> getAllPosts() {
        return sDAO.findAll();
    }

    public void deletePostById(ObjectId id){
        sDAO.deleteById(id);
    }

    public Post updatePost(ObjectId id, Post update){
        Post existingPost = sDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Update only fields that are not null
        if (update.getHeader() != null) {
            existingPost.setHeader(update.getHeader());
        }
        if (update.getTextBody() != null) {
            existingPost.setTextBody(update.getTextBody());
        }
        if (update.getComments() != null && !update.getComments().isEmpty()) {
            existingPost.setComments(update.getComments());
        }
        if (update.getLikes() != null) {
            existingPost.setLikes(update.getLikes());
        }
        if (update.getTags() != null) {
            existingPost.setTags(update.getTags());
        }
        if (update.getFlagged() != null) {
            existingPost.setFlagged(update.getFlagged());
        }
        if (update.getHelp() != null) {
            existingPost.setHelp(update.getHelp());
        }

        return sDAO.save(existingPost);
    }
}
