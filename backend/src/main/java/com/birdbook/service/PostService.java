package com.birdbook.service;

import com.birdbook.controller.UserController;
import com.birdbook.models.Post;
import com.birdbook.models.User;
import com.birdbook.repository.PostDAO;
import com.birdbook.repository.UserDAO;
import com.birdbook.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PostService {

    private final PostDAO sDAO;
    private final UserDAO userDAO;
    private final MongoTemplate mongoTemplate;

    public PostService(PostDAO sDAO, UserDAO userDAO, MongoTemplate mongoTemplate) {
        this.sDAO = sDAO;
        this.userDAO = userDAO;
        this.mongoTemplate = mongoTemplate;
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

    public Post createPost(Post newPost) {
        return sDAO.save(newPost);
    }
    
    public List<Post> getAllPostsByFriends(ObjectId userId) {
        //use userids to get friends list
        User user = userDAO.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found."));
        ObjectId[] friendIds = user.getFriends();

        if (friendIds == null || friendIds.length ==0){
            return List.of();
        }

        // fetch all friends
        List<User> friends = userDAO.findAllById(List.of(friendIds));

        //collect all post ids from friends
        List<ObjectId> allPostIds = new ArrayList<>();

        for (User friend: friends){
            if(friend.getPosts()!= null){
                allPostIds.addAll(List.of(friend.getPosts()));
            }
        }

        if (allPostIds.isEmpty()){
            return List.of();
        }

        //finally setch posts by ids
        return sDAO.findAllById(allPostIds);
    }

    public List<Post> getAllPostsByTags(Map<String,String> tags) {
        Query query = new Query();

        // Add a Criteria for each key-value pair (AND)
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            query.addCriteria(Criteria.where("tags." + key).is(value));
        }

        return mongoTemplate.find(query, Post.class);
    }
}
