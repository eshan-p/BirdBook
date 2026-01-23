package com.birdbook.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import com.birdbook.models.Bird;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.birdbook.repository.BirdDAO;

@Service
public class BirdService {
    private final BirdDAO birdDAO;
    private final MongoTemplate mongoTemplate;

    public BirdService(BirdDAO birdDAO, MongoTemplate mongoTemplate) {
        this.birdDAO = birdDAO;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Bird> searchBirds(String query) {
        Query mongoQuery = new Query();
        Criteria criteria = new Criteria().orOperator(
            Criteria.where("commonName").regex(query, "i"),
            Criteria.where("scientificName").regex(query, "i")
        );
        mongoQuery.addCriteria(criteria);
        mongoQuery.limit(20);
        return mongoTemplate.find(mongoQuery, Bird.class);
    }

    // Just for testing Spring Boot, can be removed later
    public Bird getBirdByCommonName(String commonName) {
        return birdDAO.findByCommonName(commonName);
    }

    public List<Bird> getAllBirds() {
        //System.out.println(birdDAO.findAll());
        return birdDAO.findAll();
    }

    public Bird addBird(Bird newBird, MultipartFile imageFile) {
        
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            newBird.setImageURL(imagePath);
        }

        return birdDAO.save(newBird);
    }

    //delete crud
    public void deleteBird(ObjectId id){

        if (!birdDAO.existsById(id)){
            throw new IllegalArgumentException("Bird not found.");
        }

        birdDAO.deleteById(id);
    }

    public Bird updateBird(ObjectId id, Bird birdRequest, MultipartFile image) {
        Bird existingBird = birdDAO.findById(id).orElseThrow(() -> new IllegalArgumentException("Bird not found."));

        existingBird.setCommonName(birdRequest.getCommonName());

        if (image != null && !image.isEmpty()) {
            String imagePath = saveImage(image);
            existingBird.setImageURL(imagePath);
        }

        return birdDAO.save(existingBird);
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

    //create crud
    /* public Bird addBird(Bird newBird) {
        return birdDAO.insert(newBird);
    } */

    /*public Bird updateBird(ObjectId id, Bird birdRequest) {
        Bird existingBird = birdDAO.findById(id).orElseThrow(() -> new IllegalArgumentException("Bird not found."));

        existingBird.setCommonName(birdRequest.getCommonName());
        existingBird.setImageURL(birdRequest.getImageURL());

        return birdDAO.save(existingBird);
    }*/
}