package com.birdbook.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.birdbook.models.Bird;
import com.birdbook.service.BirdService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@RestController
@RequestMapping("/birds")
@CrossOrigin(origins = "http://localhost:5173")
public class BirdController {
    private final BirdService birdService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public BirdController(BirdService birdService, ObjectMapper objectMapper, Validator validator) {
        this.birdService = birdService;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @GetMapping
    public List<Bird> getAllBirds() {
        return birdService.getAllBirds();
    }

    @GetMapping("/{commonName}")
    public Bird getBirdByCommonName(@PathVariable String commonName) {
        return birdService.getBirdByCommonName(commonName);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addBirdMultipart(
        @RequestPart("bird") String birdJson,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try{
            Bird newBird = objectMapper.readValue(birdJson, Bird.class);

            Set<ConstraintViolation<Bird>> violations = validator.validate(newBird);

            if (!violations.isEmpty()) {
                Map<String, String> errors = new HashMap<>();
                for (ConstraintViolation<Bird> v : violations) {
                    errors.put(v.getPropertyPath().toString(), v.getMessage());
                }
                return ResponseEntity.badRequest().body(errors);
            }

            return ResponseEntity.ok(birdService.addBird(newBird, image));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid bird data: " + e.getMessage());
        }
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Bird updateBirdMultipart(
        @PathVariable("id") ObjectId id,
        @RequestPart("bird") String birdJson,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try{
            Bird birdRequest = objectMapper.readValue(birdJson, Bird.class);
            return birdService.updateBird(id, birdRequest, image);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update bird", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id){

        ObjectId bId = new ObjectId(id);
        birdService.deleteBird(bId);

        return new ResponseEntity<String>("Bird deleted successfully", HttpStatus.OK);
    }

    //addBird(Bird newBird)
    /* @PostMapping("/new")
    public Bird addBird(@RequestBody Bird newBird){
        return birdService.addBird(newBird);
    }*/

    /* @PutMapping("/update/{id}")
    public ResponseEntity<Bird> updateBird(@PathVariable String id, @RequestBody Bird birdRequest){

        ObjectId userId = new ObjectId(id);
        Bird updatedBird = birdService.updateBird(userId, birdRequest);

        return ResponseEntity.ok(updatedBird);
    } */
}