package com.birdbook.controller;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.birdbook.models.Bird;
import com.birdbook.models.Group;
import com.birdbook.models.Post;
import com.birdbook.models.User;
import com.birdbook.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        
        ObjectId userId = new ObjectId(id);
        return userService.getUserById(userId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable String id) {

        ObjectId userId = new ObjectId(id);
        return userService.getFriendsList(userId);
    }

    @GetMapping("/{id}/groups")
    public List<Group> getGroups(@PathVariable String id) {

        ObjectId userId = new ObjectId(id);
        return userService.getGroupsList(userId);
    }

    @GetMapping("/{id}/posts")
    public List<Post> getPosts(@PathVariable String id) {
        ObjectId userId = new ObjectId(id);
        return userService.getPostsList(userId);
    }

    @GetMapping("/{id}/top-birds")
    public List<Map<String, ? extends Object>> getTopBirdsSighted(@PathVariable String id) {
        ObjectId userId = new ObjectId(id);
        return userService.getTopBirdsThisMonth(userId);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String id) {
        try {
            ObjectId userObjId = new ObjectId(id);
            Map<String, Object> stats = userService.getUserStats(userObjId);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<String> registerUser(@Valid @RequestBody User userRequest){ 
        
        userService.registerUser(userRequest.getUsername(), userRequest.getPassword());

        return new ResponseEntity<String>("User registered successfully", HttpStatus.CREATED);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<String> addFriend(@PathVariable String id, @PathVariable String friendId) {

        ObjectId userId = new ObjectId(id);
        ObjectId friendIdObj = new ObjectId(friendId);
        userService.addFriend(userId, friendIdObj);

        return new ResponseEntity<String>("Friend added successfully", HttpStatus.OK);
    }

    /*@PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User userRequest){

        ObjectId userId = new ObjectId(id);
        User updatedUser = userService.updateUser(userId, userRequest);

        return ResponseEntity.ok(updatedUser);
    }*/
    
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public User updateUserMultipart(
        @PathVariable("id") ObjectId id,
        @RequestPart("user") String userJson,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            User updatedUser = objectMapper.readValue(userJson, User.class);
            return userService.updateUser(id, updatedUser, image);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id){

        ObjectId userId = new ObjectId(id);
        userService.deleteUser(userId);

        return new ResponseEntity<String>("User deleted successfully", HttpStatus.OK);
    }
}
