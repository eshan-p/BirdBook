package com.birdbook.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.birdbook.models.User;
import com.birdbook.service.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        
        ObjectId userId = new ObjectId(id);
        return userService.getUserById(userId);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User userRequest){ 
        
        userService.registerUser(userRequest.getUsername(), userRequest.getPassword());

        return new ResponseEntity<String>("User registered successfully", HttpStatus.CREATED);
    }

    @PutMapping("/addFriend/{id}/{friendId}")
    public ResponseEntity<String> addFriend(@PathVariable String id, @PathVariable String friendId) {

        ObjectId userId = new ObjectId(id);
        ObjectId friendIdObj = new ObjectId(friendId);
        userService.addFriend(userId, friendIdObj);

        return new ResponseEntity<String>("Friend added successfully", HttpStatus.OK);
    }

    @GetMapping("/getFriends/{id}")
    public List<User> getFriends(@PathVariable String id) {
        
        ObjectId userId = new ObjectId(id);
        return userService.getFriendsList(userId);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User userRequest){

        ObjectId userId = new ObjectId(id);
        User updatedUser = userService.updateUser(userId, userRequest);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id){

        ObjectId userId = new ObjectId(id);
        userService.deleteUser(userId);

        return new ResponseEntity<String>("User deleted successfully", HttpStatus.OK);
    }
}
