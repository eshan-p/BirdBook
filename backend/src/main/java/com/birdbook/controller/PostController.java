package com.birdbook.controller;

import com.birdbook.models.Comment;
import com.birdbook.models.Post;
import com.birdbook.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/sightings")
public class PostController {

    private final PostService sService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public PostController(PostService sightService, ObjectMapper objectMapper, Validator validator) {
        this.sService = sightService;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return sService.getAllPosts();
    }

    @GetMapping("/user/{userId}")
    public List<Post> getAllPostsByFriends(@PathVariable ObjectId userId) {
        return sService.getAllPostsByFriends(userId);
    }

    //GET /sightings/tags?location=Texas&bird=BlueJay&etc=etc
    @GetMapping("/tags")
    public List<Post> getAllPostsByTags(@RequestParam Map<String,String> tags) {
        return sService.getAllPostsByTags(tags);
    }

    // GET /sightings/[id]
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable ObjectId id) {
        return sService.getPostById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Multipart post with optional image; can still pass JSON-only post but frontend request must still be multipart/form-data
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPostMultipart(
            @RequestPart("post") String postJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication
    ) {
        try {
            ObjectId userId = new ObjectId(authentication.getPrincipal().toString());

            Post newPost = objectMapper.readValue(postJson, Post.class);
            newPost.setUserId(userId);

            Set<ConstraintViolation<Post>> violations = validator.validate(newPost);

            if (!violations.isEmpty()) {
                Map<String, String> errors = new HashMap<>();
                for (ConstraintViolation<Post> v : violations) {
                    errors.put(v.getPropertyPath().toString(), v.getMessage());
                }
                return ResponseEntity.badRequest().body(errors);
            }

            return ResponseEntity.ok(sService.createPost(newPost, image));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid post data: " + e.getMessage());
        }
    }

    // Multipart post with optional image; can still update post w/ JSON-only data but frontend request must still be multipart/form-data
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Post updatePostMultipart(
            @PathVariable("id") ObjectId id,
            @RequestPart("post") String postJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            Post updatedPost = objectMapper.readValue(postJson, Post.class);
            return sService.updatePost(id, updatedPost, image);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update post", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable String id){
        ObjectId pId = new ObjectId(id);
        sService.deletePostById(pId);
        return new ResponseEntity<String>("Post deleted successfully", HttpStatus.OK);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable ObjectId id, @RequestBody Comment comment) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            ObjectId userId = new ObjectId(auth.getName());

            comment.setUserId(userId);
            comment.setTimestamp(new Date());

            return ResponseEntity.ok(sService.addComment(id, comment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/comments")
    public ResponseEntity<?> updateComment(@PathVariable ObjectId id, @RequestBody Comment updatedComment) {
        try {

            ObjectId userId = new ObjectId(SecurityContextHolder.getContext().getAuthentication().getName());
            return ResponseEntity.ok(sService.updateComment(id, userId, updatedComment));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/comments")
    public ResponseEntity<?> deleteComment(@PathVariable ObjectId id, @RequestBody Comment comment) {
        try {
            
            ObjectId userId = new ObjectId(SecurityContextHolder.getContext().getAuthentication().getName());
            return ResponseEntity.ok(sService.deleteComment(id, userId, comment.getTimestamp()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /*@PostMapping
    public String createPost(@RequestBody Post newPost){
        Post success =  sService.createPost(newPost);
        return String.format("{ \"id\" : %s }", success.getId().toHexString());
    }*/

    //updatePost(ObjectId id, String header)
    /* @PatchMapping("/{id}")
    public Post updatePost(@PathVariable("id") ObjectId id, @RequestBody Post updatedPost){
        return sService.updatePost(id,updatedPost);
    } */
}
