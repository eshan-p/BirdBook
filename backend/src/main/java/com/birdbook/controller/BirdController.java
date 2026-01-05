package com.birdbook.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    public List<Bird> getAllBirds() {
        return birdService.getAllBirds();
    }

    // just for testing Spring Boot, can be removed later
    @GetMapping("/by-name")
    public Bird getBirdByCommonName(String commonName) {
        return birdService.getBirdByCommonName(commonName);
    }
}
