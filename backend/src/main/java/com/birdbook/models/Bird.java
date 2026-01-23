package com.birdbook.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Document(collection = "birds")
public class Bird {

    @Id
    private ObjectId id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 100, message = "Bird name must be between 2 and 100 characters")
    private String commonName;

    private String scientificName;

    private String imageURL;

    public Bird() {
    }

    public Bird(ObjectId id, String commonName, String scientificName, String imageURL) {
        this.id = id;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.imageURL = imageURL;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}