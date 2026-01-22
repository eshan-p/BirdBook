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
@CrossOrigin(origins = "http://localhost:5173")
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

    @GetMapping("/tags")
    public List<Post> getAllPostsByTags(@RequestParam Map<String,String> tags) {
        return sService.getAllPostsByTags(tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable ObjectId id) {
        return sService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPostMultipart(
            @RequestPart("post") String postJson,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
        }

        try {
            ObjectId userId = new ObjectId(authentication.getName());

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

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePostMultipart(
            @PathVariable("id") ObjectId id,
            @RequestPart("post") String postJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            Post updatedPost = objectMapper.readValue(postJson, Post.class);
            return ResponseEntity.ok(sService.updatePost(id, updatedPost, image));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update post");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable String id){
        ObjectId pId = new ObjectId(id);
        sService.deletePostById(pId);
        return ResponseEntity.ok("Post deleted successfully");
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable ObjectId id, @RequestBody Comment comment) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

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
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            ObjectId userId = new ObjectId(auth.getName());
            return ResponseEntity.ok(sService.updateComment(id, userId, updatedComment));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/comments")
    public ResponseEntity<?> deleteComment(@PathVariable ObjectId id, @RequestBody Comment comment) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }

            ObjectId userId = new ObjectId(auth.getName());
            return ResponseEntity.ok(
                    sService.deleteComment(id, userId, comment.getTimestamp())
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
