package com.birdbook.controller;

import java.util.List;

import com.birdbook.models.User;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //addBird(Bird newBird)
    @PostMapping("/new")
    public Bird addBird(@RequestBody Bird newBird){
        return birdService.addBird(newBird);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id){

        ObjectId bId = new ObjectId(id);
        birdService.deleteBird(bId);

        return new ResponseEntity<String>("Bird deleted successfully", HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Bird> updateBird(@PathVariable String id, @RequestBody Bird birdRequest){

        ObjectId userId = new ObjectId(id);
        Bird updatedBird = birdService.updateBird(userId, birdRequest);

        return ResponseEntity.ok(updatedBird);
    }
}
