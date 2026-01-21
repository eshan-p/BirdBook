package com.birdbook.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Document(collection = "groups")
public class Group {
    
    @Id
    private ObjectId id;

    @NotBlank(message = "Group Name cannot be blank")
    @Size(max = 40, message = "Group Name cannot exceed 40 characters.")
    private String name;

    private ObjectId ownerId;
    private ObjectId[] members;
    private ObjectId[] requests;

    public Group(){

    }

    public Group(String name, ObjectId ownerId) {
        this.id = new ObjectId();
        this.name = name;
        this.ownerId = ownerId;
        this.members = new ObjectId[0];
        this.requests = new ObjectId[0];
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
     * @return String return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return ObjectId return the ownerId
     */
    public ObjectId getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId the ownerId to set
     */
    public void setOwnerId(ObjectId ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * @return ObjectId[] return the members
     */
    public ObjectId[] getMembers() {
        return members;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(ObjectId[] members) {
        this.members = members;
    }

    /**
     * @return ObjectId[] return the requests
     */
    public ObjectId[] getRequests() {
        return requests;
    }

    /**
     * @param requests the requests to set
     */
    public void setRequests(ObjectId[] requests) {
        this.requests = requests;
    }
}
