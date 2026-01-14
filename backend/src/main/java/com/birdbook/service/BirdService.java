package com.birdbook.service;

import java.util.List;

import com.birdbook.models.Bird;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;;
import com.birdbook.repository.BirdDAO;

@Service
public class BirdService {
    private final BirdDAO birdDAO;

    public BirdService(BirdDAO birdDAO) {
        this.birdDAO = birdDAO;
    }

    // Just for testing Spring Boot, can be removed later
    public Bird getBirdByCommonName(String commonName) {
        return birdDAO.findByCommonName(commonName);
    }

    public List<Bird> getAllBirds() {
        //System.out.println(birdDAO.findAll());
        return birdDAO.findAll();
    }

    //create crud
    public Bird addBird(Bird newBird) {
        return birdDAO.insert(newBird);
    }

    //delete crud
    public void deleteBird(ObjectId id){

        if (!birdDAO.existsById(id)){
            throw new IllegalArgumentException("Bird not found.");
        }

        birdDAO.deleteById(id);
    }

    public Bird updateBird(ObjectId id, Bird birdRequest) {
        Bird existingBird = birdDAO.findById(id).orElseThrow(() -> new IllegalArgumentException("Bird not found."));

        existingBird.setCommonName(birdRequest.getCommonName());
        existingBird.setImageURL(birdRequest.getImageURL());

        return birdDAO.save(existingBird);
    }
}