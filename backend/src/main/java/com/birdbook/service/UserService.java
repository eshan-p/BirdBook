package com.birdbook.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.birdbook.models.User;
import com.birdbook.repository.UserDAO;

@Service
public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public User getUserById(ObjectId id){
        return userDAO.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void registerUser(String username, String password){

        if (userDAO.findByUsername(username).isPresent()){
            throw new IllegalArgumentException("Username already taken.");
        }

        User newUser = new User(username, password);
        userDAO.insert(newUser);
    }

    /* public User updateUser(ObjectId id, User updatedData){

        User existingUser = userDAO.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found."));

        existingUser.setUsername(updatedData.getUsername());
        existingUser.setPassword(updatedData.getPassword());

        return userDAO.save(existingUser);
    } */

    public User updateUser(ObjectId id, User updatedUser, MultipartFile imageFile){
        User existingUser = userDAO.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found."));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPassword(updatedUser.getPassword());

        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            existingUser.setProfilePic(imagePath);
        }

        return userDAO.save(existingUser);
    }

    public void deleteUser(ObjectId id){

        if (!userDAO.existsById(id)){
            throw new IllegalArgumentException("User not found.");
        }

        userDAO.deleteById(id);
    }

    public void addFriend(ObjectId userId, ObjectId friendId) {
        User user = userDAO.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found."));
        User friend = userDAO.findById(friendId).orElseThrow(() -> new IllegalArgumentException("Friend not found."));

        ObjectId[] currentFriends = user.getFriends();
        ObjectId[] updatedFriends = new ObjectId[currentFriends.length + 1];
        System.arraycopy(currentFriends, 0, updatedFriends, 0, currentFriends.length);
        updatedFriends[currentFriends.length] = friendId;

        user.setFriends(updatedFriends);
        userDAO.save(user);
    }

    public List<User> getFriendsList(ObjectId userId) {
        User user = userDAO.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found."));
        ObjectId[] friendIds = user.getFriends();

        return userDAO.findAllById(List.of(friendIds));
    } 
    
    // Helper method to save image file for adding/updating a post; returns the file path
    private String saveImage(MultipartFile imageFile){
        try {

            String uploadDir = "images";
            Files.createDirectories(Paths.get(uploadDir));

            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            Files.copy(imageFile.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            return "/" + uploadDir + "/" + fileName;

        } catch (IOException e){
            throw new RuntimeException("Failed to store image", e);
        }
    }
}
