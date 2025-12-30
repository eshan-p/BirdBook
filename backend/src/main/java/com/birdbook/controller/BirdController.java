package com.birdbook.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.birdbook.models.Bird;
import com.birdbook.service.BirdService;

@RestController
@RequestMapping("/birds")
public class BirdController {
    private final BirdService birdService;

    public BirdController(BirdService birdService) {
        this.birdService = birdService;
    }

    public List<Bird> getAllBirds() {
        return birdService.getAllBirds();
    }
}
