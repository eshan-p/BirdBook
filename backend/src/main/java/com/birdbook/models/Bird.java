package com.birdbook.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "birds")
public class Bird {

    @Id
    private ObjectId id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 100, message = "Bird name must be between 2 and 100 characters")
    private String commonName;

    private String scientificName;

    private String imageURL;

    private List<Double> location;

    public Bird() {}

    public Bird(ObjectId id, String commonName, String imageURL) {
        this.id = id;
        this.commonName = commonName;
        this.imageURL = imageURL;
    }

    public Bird(
            ObjectId id,
            String commonName,
            String scientificName,
            String imageURL,
            List<Double> location
    ) {
        this.id = id;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.imageURL = imageURL;
        this.location = location;
    }

    // GETTERS
    public ObjectId getId() {
        return id;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public List<Double> getLocation() {
        return location;
    }

    // ADD THIS METHOD (string id for frontend routing)
    @JsonProperty("id")
    public String getIdAsString() {
        return id != null ? id.toHexString() : null;
    }

    // SETTERS
    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }
}
