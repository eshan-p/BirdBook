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

    public List<Bird> getAllBirds() {
        System.out.println(birdDAO.findAll());
        return birdDAO.findAll();
    }
}
