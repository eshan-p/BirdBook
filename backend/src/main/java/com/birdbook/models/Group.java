package com.birdbook.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "groups")
public class Group {

    @Id
    private ObjectId id;

    @NotBlank(message = "Group Name cannot be blank")
    @Size(max = 40, message = "Group Name cannot exceed 40 characters.")
    private String name;

    private PostUser owner;
    private List<PostUser> members;
    private List<PostUser> requests;

    public Group() {}

    public Group(String name, PostUser owner) {
        this.id = new ObjectId();
        this.name = name;
        this.owner = owner;
        this.members = new ArrayList<>();
        this.requests = new ArrayList<>();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PostUser getOwner() {
        return owner;
    }

    public void setOwner(PostUser owner) {
        this.owner = owner;
    }

    public List<PostUser> getMembers() {
        return members;
    }

    public void setMembers(List<PostUser> members) {
        this.members = members;
    }

    public List<PostUser> getRequests() {
        return requests;
    }

    public void setRequests(List<PostUser> requests) {
        this.requests = requests;
    }
}
