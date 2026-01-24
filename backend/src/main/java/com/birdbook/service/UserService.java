package com.birdbook.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.*;

import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.birdbook.models.Group;
import com.birdbook.models.Post;
import com.birdbook.models.User;
import com.birdbook.repository.GroupDAO;
import com.birdbook.repository.PostDAO;
import com.birdbook.repository.UserDAO;

@Service
public class UserService {

    private final GroupDAO groupDAO;
    private final UserDAO userDAO;
    private final PostDAO postDAO;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserDAO userDAO, PostDAO postDAO, PasswordEncoder passwordEncoder, GroupDAO groupDAO){
        this.userDAO = userDAO;
        this.postDAO = postDAO;
        this.passwordEncoder = passwordEncoder;
        this.groupDAO = groupDAO;
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public Optional<User> getByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public User getUserById(ObjectId id){
        return userDAO.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<User> searchUsersByUsername(String query) {
        List<User> allUsers = userDAO.findAll();
        
        return allUsers.stream()
            .filter(user -> user.getUsername().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());
    }

    public List<Group> getGroupsList(ObjectId userId) {
        User user = userDAO.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found."));
        ObjectId[] groupIds = user.getGroups();

        return groupDAO.findAllById(List.of(groupIds));
    } 
    
    public List<Post> getPostsList(ObjectId userId) {
        User user = userDAO.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        ObjectId[] postIds = user.getPosts();
        return postDAO.findAllById(List.of(postIds));
    }

    public List<Map<String, ? extends Object>> getTopBirdsThisMonth(ObjectId userId) {
        List<Post> userPosts = postDAO.findByUserId(userId);
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return userPosts.stream()
            .filter(post -> post.getTimestamp() != null && post.getTimestamp().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime().isAfter(startOfMonth))
            .collect(Collectors.groupingBy(Post::getBird, Collectors.counting()))
            .entrySet().stream()
            .sorted((a,b) -> b.getValue().compareTo(a.getValue()))
            .limit(5)
            .map(entry -> Map.of("bird", entry.getKey(), "count", entry.getValue()))
            .collect(Collectors.toList());
    }

    public User registerUser(String username, String password){

        if (userDAO.findByUsername(username).isPresent()){
            throw new IllegalArgumentException("Username already taken.");
        }

        String hashedPassword = passwordEncoder.encode(password);
        User newUser = new User(username, hashedPassword);

        userDAO.insert(newUser);

        return newUser;
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

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

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

    public void removeFriend(ObjectId userId, ObjectId friendId) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        ObjectId[] currentFriends = user.getFriends();
        
        ObjectId[] updatedFriends = java.util.Arrays.stream(currentFriends)
            .filter(id -> !id.equals(friendId))
            .toArray(ObjectId[]::new);
        
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

    // User stats methods below

    public Map<String, Object> getUserStats(ObjectId userId) {
        User user = userDAO.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Post> posts = postDAO.findAllById(List.of(user.getPosts()));

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalSpottings", posts.size());
        stats.put("firstSightingDate", calculateFirstSighting(posts));
        stats.put("uniqueBirdsSpotted", calculateUniqueBirds(posts));
        stats.put("mostSpottedBird", calculateMostSpottedBird(posts));
        stats.put("topBirdsAllTime", topBirdsAllTime(posts));
        stats.put("topBirdsThisMonth", topBirdsThisMonth(posts));
        stats.put("badges", calculateBadges(stats));

        return stats;
    }

    private Date calculateFirstSighting(List<Post> posts) {
        return posts.stream()
                .map(Post::getTimestamp)
                .min(Date::compareTo)
                .orElse(null);
    }

    private int calculateUniqueBirds(List<Post> posts){
        return (int) posts.stream()
            .map(Post::getBird)
            .filter(Objects::nonNull)
            .distinct()
            .count();
    }

    private ObjectId calculateMostSpottedBird(List<Post> posts) {
        return posts.stream()
            .map(Post::getBird)
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(b -> b, Collectors.counting()))
            .entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    private List<Map<String, Object>> topBirdsAllTime(List<Post> posts) {
        Map<ObjectId, Long> counts = posts.stream()
            .map(Post::getBird)
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(b -> b, Collectors.counting()));

        return counts.entrySet().stream()
            .sorted(Map.Entry.<ObjectId, Long>comparingByValue().reversed())
            .map(entry -> {
                Map<String, Object> map = new HashMap<>();
                map.put("birdId", entry.getKey());
                map.put("count", entry.getValue());
                return map;
            })
            .toList();
    }


    private List<Map<String, Object>> topBirdsThisMonth(List<Post> posts) {
        YearMonth currentMonth = YearMonth.now();

        Map<ObjectId, Long> counts = posts.stream()
            .filter(post -> {
                LocalDate date = post.getTimestamp()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
                    return YearMonth.from(date).equals(currentMonth);
            })
            .map(Post::getBird)
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(b -> b, Collectors.counting()));

        return counts.entrySet().stream()
            .sorted(Map.Entry.<ObjectId, Long>comparingByValue().reversed())
            .map(entry -> {
                Map<String, Object> map = new HashMap<>();
                map.put("birdId", entry.getKey());
                map.put("count", entry.getValue());
                return map;
            })
            .toList();
    }

    public List<String> calculateBadges(Map<String, Object> stats) {
        List<String> badges = new ArrayList<>();

        int totalSpottings = (int) stats.getOrDefault("totalSpottings", 0);
        int uniqueBirds = (int) stats.getOrDefault("uniqueBirdsSpotted", 0);

        if (totalSpottings >= 1) {
            badges.add("First Sighting");
        }
        else if (uniqueBirds >= 10) {
            badges.add("Bird Collector");
        }
        else if (totalSpottings >= 50) {
            badges.add("Popular Spotter");
        }
        
        return badges;
    }
}
