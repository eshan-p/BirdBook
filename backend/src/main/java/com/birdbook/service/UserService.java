package com.birdbook.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

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

    public User updateUser(ObjectId id, User updatedData){

        User existingUser = userDAO.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found."));

        existingUser.setUsername(updatedData.getUsername());
        existingUser.setPassword(updatedData.getPassword());

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
}
