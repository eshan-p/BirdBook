package com.birdbook.controller;

import com.birdbook.models.Post;
import com.birdbook.service.PostService;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/user/{userId}")
    public List<Post> getAllPostsByFriends(@PathVariable ObjectId userId) {
        return sService.getAllPostsByFriends(userId);
    }

    //GET /sightings/by-tags?location=Texas&bird=BlueJay&etc=etc
    @GetMapping("/by-tags")
    public List<Post> getAllPostsByTags(@RequestParam Map<String,String> tags) {
        return sService.getAllPostsByTags(tags);
    }

    // GET /sightings/[id]
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

    @PostMapping
    public String createPost(@RequestBody Post newPost){
        Post success =  sService.createPost(newPost);
        return String.format("{ \"id\" : %s }", success.getId().toHexString());
    }
}
