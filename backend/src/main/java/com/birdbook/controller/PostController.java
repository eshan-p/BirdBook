package com.birdbook.controller;

import com.birdbook.models.Post;
import com.birdbook.service.PostService;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sightings")
public class PostController {

    private final PostService sService;

    public PostController(PostService sightService) {
        this.sService = sightService;
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return sService.getAllPosts();
    }

    // just for testing Spring Boot, can be removed later
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable ObjectId id) {
        return sService.getPostById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    //updatePost(ObjectId id, String header)
    @PatchMapping("/{id}")
    public Post updatePost(@PathVariable("id") ObjectId id, @RequestBody Post updatedPost){
        return sService.updatePost(id,updatedPost);
    }
}
