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

@RestController
@RequestMapping("/api/birds")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
public class BirdController {

    private final BirdService birdService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public BirdController(
            BirdService birdService,
            ObjectMapper objectMapper,
            Validator validator
    ) {
        this.birdService = birdService;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Bird>> searchBirds(@RequestParam String query) {
        System.out.println("!=========HIT!=========");
        if(query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Bird> results = birdService.searchBirds(query.trim());
        return ResponseEntity.ok(results);
    }

    // GET ALL BIRDS
    @GetMapping
    public ResponseEntity<List<Bird>> getAllBirds() {
        return ResponseEntity.ok(birdService.getAllBirds());
    }

    // GET BIRD BY ID (USED BY BirdDetail PAGE)
    @GetMapping("/{id}")
    public ResponseEntity<Bird> getBirdById(@PathVariable String id) {
        try {
            Bird bird = birdService.getBirdById(id);
            return ResponseEntity.ok(bird);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ADD BIRD (MULTIPART)
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

            Bird savedBird = birdService.addBird(newBird, image);
            return ResponseEntity.ok(savedBird);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid bird data: " + e.getMessage());
        }
    }

    // UPDATE BIRD
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Bird> updateBirdMultipart(
            @PathVariable String id,
            @RequestPart("bird") String birdJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            Bird birdRequest = objectMapper.readValue(birdJson, Bird.class);
            Bird updatedBird = birdService.updateBird(
                    new ObjectId(id),
                    birdRequest,
                    image
            );
            return ResponseEntity.ok(updatedBird);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE BIRD
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBird(@PathVariable String id) {
        birdService.deleteBird(new ObjectId(id));
        return ResponseEntity.ok("Bird deleted successfully");
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
