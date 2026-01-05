package com.birdbook.util;

import java.util.Arrays;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoCollection;

@Component
public class MongoDataInitializer implements CommandLineRunner {
    ObjectId user1 = new ObjectId();
    ObjectId user2 = new ObjectId();
    ObjectId user3 = new ObjectId();
    ObjectId user4 = new ObjectId();
    ObjectId group1 = new ObjectId();
    ObjectId group2 = new ObjectId();
    ObjectId comment1 = new ObjectId();
    ObjectId comment2 = new ObjectId();
    ObjectId post1 = new ObjectId();
    ObjectId post2 = new ObjectId();


    @Override
    public void run(String... args) throws Exception {
        MongoCollection<Document> birdsCollection = 
            ConnectionHandler.getDatabase().getCollection("birds");
        if(birdsCollection.countDocuments() == 0) {
            populateBirds(birdsCollection);
            System.out.println("Bird data initialized successfully!");
        } else {
            System.out.println("Bird collection already populated, skipping initialization.");
        }
    }

    private void populateBirds(MongoCollection<Document> collection) {
        Document[] birds = {
            new Document("_id", new ObjectId())
                .append("commonName", "American Robin")
                .append("imageURL", "https://example.com/images/american-robin.jpg"),
            
            new Document("_id", new ObjectId())
                .append("commonName", "Northern Cardinal")
                .append("imageURL", "https://example.com/images/northern-cardinal.jpg"),
            
            new Document("_id", new ObjectId())
                .append("commonName", "Black-capped Chickadee")
                .append("imageURL", "https://example.com/images/chickadee.jpg"),
            
            new Document("_id", new ObjectId())
                .append("commonName", "Blue Jay")
                .append("imageURL", "https://example.com/images/blue-jay.jpg"),
            
            new Document("_id", new ObjectId())
                .append("commonName", "House Finch")
                .append("imageURL", "https://example.com/images/house-finch.jpg"),
            
            new Document("_id", new ObjectId())
                .append("commonName", "Downy Woodpecker")
                .append("imageURL", "https://example.com/images/downy-woodpecker.jpg"),
            
            new Document("_id", new ObjectId())
                .append("commonName", "Tufted Titmouse")
                .append("imageURL", "https://example.com/images/tufted-titmouse.jpg"),
            
            new Document("_id", new ObjectId())
                .append("commonName", "American Goldfinch")
                .append("imageURL", "https://example.com/images/american-goldfinch.jpg")
        };  
        collection.insertMany(Arrays.asList(birds));
    }

    private void populateGroups(MongoCollection<Document> collection) {
        Document groups[] = {
            new Document("_id", group1)
            .append("groupName", "DFW birders")
            .append("owner", user1.toString())
            .append("members", Arrays.asList(user1.toString(), user2.toString()))
            .append("requests", Arrays.asList()),

            new Document("_id", group2)
            .append("groupName", "Coastal Bird Committee")
            .append("owner", user2.toString())
            .append("members", Arrays.asList(user2.toString(), user1.toString(), user3.toString()))
            .append("requests", Arrays.asList(user4.toString()))
        };
        collection.insertMany(Arrays.asList(groups));
    }

    private void populatePosts(MongoCollection<Document> collection) {

    }

    private void populateUsers(MongoCollection<Document> collection) {
        //TODO, link to actual groups / posts
        Document users[] = {
            new Document("_id", user1)
            .append("username", "birdwatcher_beth")
            .append("password", "password123")
            .append("friends", Arrays.asList(user2.toString(), user3.toString()))
            .append("posts", Arrays.asList())
            .append("groups", Arrays.asList()),

            new Document("_id", user2)
            .append("username", "nature_nancy")
            .append("password", "pwrd2941j")
            .append("friends", Arrays.asList(user1.toString(), user4.toString()))
            .append("posts", Arrays.asList())
            .append("groups", Arrays.asList()),

            new Document("_id", user3)
            .append("username", "ornithology_oscar")
            .append("password", "kjlbgdra78")
            .append("friends", Arrays.asList(user1.toString()))
            .append("posts", Arrays.asList())
            .append("groups", Arrays.asList()),

            new Document("_id", user4)
            .append("username", "spotter_scott")
            .append("password", "fisohaf12")
            .append("friends", Arrays.asList(user2.toString()))
            .append("posts", Arrays.asList())
            .append("groups", Arrays.asList()),
        };
        collection.insertMany(Arrays.asList(users));
    }
}