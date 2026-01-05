package com.birdbook.util;

import java.time.Instant;
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

    ObjectId bird1 = new ObjectId();
    ObjectId bird2 = new ObjectId();
    ObjectId bird3 = new ObjectId();
    ObjectId bird4 = new ObjectId();
    ObjectId bird5 = new ObjectId();
    ObjectId bird6 = new ObjectId();
    ObjectId bird7 = new ObjectId();
    ObjectId bird8 = new ObjectId();

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

        MongoCollection<Document> usersCollection = 
            ConnectionHandler.getDatabase().getCollection("users");
        if(usersCollection.countDocuments() == 0) {
            populateUsers(usersCollection);
            System.out.println("User data initialized successfully!");
        } else {
            System.out.println("User collection already populated, skipping initialization.");
        }

        MongoCollection<Document> groupsCollection = 
            ConnectionHandler.getDatabase().getCollection("groups");
        if(groupsCollection.countDocuments() == 0) {
            populateGroups(groupsCollection);
            System.out.println("Group data initialized successfully!");
        } else {
            System.out.println("Group collection already populated, skipping initialization.");
        }

        MongoCollection<Document> postsCollection = 
            ConnectionHandler.getDatabase().getCollection("posts");
        if(postsCollection.countDocuments() == 0) {
            populatePosts(postsCollection);
            System.out.println("Post data initialized successfully!");
        } else {
            System.out.println("Post collection already populated, skipping initialization.");
        }
    }

    private void populateBirds(MongoCollection<Document> collection) {
        Document[] birds = {
            new Document("_id", bird1.toString())
                .append("commonName", "American Robin")
                .append("imageURL", "https://example.com/images/american-robin.jpg"),
            new Document("_id", bird2.toString())
                .append("commonName", "Northern Cardinal")
                .append("imageURL", "https://example.com/images/northern-cardinal.jpg"),
            new Document("_id", bird3.toString())
                .append("commonName", "Black-capped Chickadee")
                .append("imageURL", "https://example.com/images/chickadee.jpg"),
            new Document("_id", bird4.toString())
                .append("commonName", "Blue Jay")
                .append("imageURL", "https://example.com/images/blue-jay.jpg"),
            new Document("_id", bird5.toString())
                .append("commonName", "House Finch")
                .append("imageURL", "https://example.com/images/house-finch.jpg"),
            new Document("_id", bird6.toString())
                .append("commonName", "Downy Woodpecker")
                .append("imageURL", "https://example.com/images/downy-woodpecker.jpg"),
            new Document("_id", bird7.toString())
                .append("commonName", "Tufted Titmouse")
                .append("imageURL", "https://example.com/images/tufted-titmouse.jpg"),
            new Document("_id", bird8.toString())
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
            .append("members", Arrays.asList(user1.toString(), user2.toString(), user3.toString()))
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
        Document posts[] = {
            new Document("_id", post1)
            .append("header", "Rare Tufted Titmouse spotting!")
            .append("tags", new Document()
                .append("location", "San Antonio, TX"))
                .append("bird", bird7.toString())
                .append("flagged", false)
                .append("group", group1.toString())
                .append("help", false)
            .append("likes", Arrays.asList(user2.toString()))
            .append("image", "TODO")
            .append("textBody", "Found this Tufted Titmouse, let me know what you guys think!")
            .append("timestamp", Instant.now())
            .append("comments", Arrays.asList(
                new Document("userId", user2.toString())
                    .append("textBody", "Great shot!")
                    .append("timestamp", Instant.now()),
                
                new Document("userId", user1.toString())
                    .append("textBody", "Awesome bird! My favorite!")
                    .append("timestamp", Instant.now())
            )),

            new Document("_id", post2)
            .append("header", "Downy Woodpecker at the park")
            .append("tags", new Document()
                .append("location", "Richardson, TX"))
                .append("bird", bird6.toString())
                .append("flagged", false)
                .append("group", group2.toString())
                .append("help", false)
            .append("likes", Arrays.asList(user4.toString(), user3.toString(), user1.toString()))
            .append("image", "TODO")
            .append("textBody", "Found this Tufted Titmouse, let me know what you guys think!")
            .append("timestamp", Instant.now())
            .append("comments", Arrays.asList(
                new Document("userId", user4.toString())
                    .append("textBody", "One of my favorite parks to go spotting")
                    .append("timestamp", Instant.now()),
                
                new Document("userId", user3.toString())
                    .append("textBody", "Great work!")
                    .append("timestamp", Instant.now())
            ))
        };
        collection.insertMany(Arrays.asList(posts));
    }

    private void populateUsers(MongoCollection<Document> collection) {
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
            .append("posts", Arrays.asList(post2.toString()))
            .append("groups", Arrays.asList(group1.toString(), group2.toString())),

            new Document("_id", user3)
            .append("username", "ornithology_oscar")
            .append("password", "kjlbgdra78")
            .append("friends", Arrays.asList(user1.toString()))
            .append("posts", Arrays.asList(post1.toString()))
            .append("groups", Arrays.asList(group1.toString(), group2.toString())),

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