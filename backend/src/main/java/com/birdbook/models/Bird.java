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

    public Bird(ObjectId id, String commonName, String scientificName, String imageURL) {
        this.id = id;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.imageURL = imageURL;
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
     * @return String return the commonName
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * @param commonName the commonName to set
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /**
     * @return String return the imageURL
     */
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}