package com.birdbook.controller;

import com.birdbook.models.Bird;
import com.birdbook.service.BirdService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
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

    // GET ALL BIRDS
    @GetMapping
    public List<Bird> getAllBirds() {
        return birdService.getAllBirds();
    }

    // GET BIRD BY ID (needed for BirdDetail page)
    @GetMapping("/{id}")
    public Bird getBirdById(@PathVariable String id) {
        return birdService.getBirdById(id);
    }

    // GET BIRD BY COMMON NAME (already existed)
    @GetMapping("/{commonName}")
    public Bird getBirdByCommonName(@PathVariable String commonName) {
        return birdService.getBirdByCommonName(commonName);
    }

    // ADD BIRD (multipart)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addBirdMultipart(
            @RequestPart("bird") String birdJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
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

    // UPDATE BIRD (multipart)
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Bird updateBirdMultipart(
            @PathVariable ObjectId id,
            @RequestPart("bird") String birdJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            Bird birdRequest = objectMapper.readValue(birdJson, Bird.class);
            return birdService.updateBird(id, birdRequest, image);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update bird", e);
        }
    }

    // DELETE BIRD
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBird(@PathVariable String id) {
        ObjectId objectId = new ObjectId(id);
        birdService.deleteBird(objectId);
        return new ResponseEntity<>("Bird deleted successfully", HttpStatus.OK);
    }
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
