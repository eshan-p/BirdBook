package com.birdbook.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {
    
    @Id
    private ObjectId id;
    private String username;
    private String password;
    private ObjectId[] friends;
    private ObjectId[] posts;
    private ObjectId[] groups;

    public User(){

    }

    public User(String username, String password) {
        this.id = new ObjectId();
        this.username = username;
        this.password = password;
        this.friends = new ObjectId[0];
        this.posts = new ObjectId[0];
        this.groups = new ObjectId[0];
    }

    public User(ObjectId id, String username, String password, ObjectId[] friends, ObjectId[] posts, ObjectId[] groups) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.friends = friends;
        this.posts = posts;
        this.groups = groups;
    }

    /**
     * @return ObjectId return the id
     */
    public ObjectId getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(ObjectId id) {
        this.id = id;
    }

    /**
     * @return String return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return String return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return ObjectId[] return the friends
     */
    public ObjectId[] getFriends() {
        return friends;
    }

    /**
     * @param friends the friends to set
     */
    public void setFriends(ObjectId[] friends) {
        this.friends = friends;
    }

    /**
     * @return ObjectId[] return the posts
     */
    public ObjectId[] getPosts() {
        return posts;
    }

    /**
     * @param posts the posts to set
     */
    public void setPosts(ObjectId[] posts) {
        this.posts = posts;
    }

    /**
     * @return ObjectId[] return the groups
     */
    public ObjectId[] getGroups() {
        return groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(ObjectId[] groups) {
        this.groups = groups;
    }

}
