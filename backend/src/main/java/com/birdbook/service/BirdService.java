package com.birdbook.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.birdbook.models.Bird;
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
        System.out.println(birdDAO.findAll());
        return birdDAO.findAll();
    }
}