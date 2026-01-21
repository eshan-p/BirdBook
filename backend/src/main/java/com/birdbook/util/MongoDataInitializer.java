package com.birdbook.util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
        }

        MongoCollection<Document> usersCollection = 
            ConnectionHandler.getDatabase().getCollection("users");
        if(usersCollection.countDocuments() == 0) {
            populateUsers(usersCollection);
            System.out.println("User data initialized successfully!");
        }

        MongoCollection<Document> groupsCollection = 
            ConnectionHandler.getDatabase().getCollection("groups");
        if(groupsCollection.countDocuments() == 0) {
            populateGroups(groupsCollection);
            System.out.println("Group data initialized successfully!");
        }

        MongoCollection<Document> postsCollection = 
            ConnectionHandler.getDatabase().getCollection("posts");
        if(postsCollection.countDocuments() == 0) {
            populatePosts(postsCollection);
            System.out.println("Post data initialized successfully!");
        }
    }

    private void populateBirds(MongoCollection<Document> collection) {
        Document[] birds = {
            new Document("_id", bird1).append("commonName", "American Robin").append("imageURL", "https://example.com/images/american-robin.jpg"),
            new Document("_id", bird2).append("commonName", "Northern Cardinal").append("imageURL", "https://example.com/images/northern-cardinal.jpg"),
            new Document("_id", bird3).append("commonName", "Black-capped Chickadee").append("imageURL", "https://example.com/images/chickadee.jpg"),
            new Document("_id", bird4).append("commonName", "Blue Jay").append("imageURL", "https://example.com/images/blue-jay.jpg"),
            new Document("_id", bird5).append("commonName", "House Finch").append("imageURL", "https://example.com/images/house-finch.jpg"),
            new Document("_id", bird6).append("commonName", "Downy Woodpecker").append("imageURL", "https://example.com/images/downy-woodpecker.jpg"),
            new Document("_id", bird7).append("commonName", "Tufted Titmouse").append("imageURL", "https://example.com/images/tufted-titmouse.jpg"),
            new Document("_id", bird8).append("commonName", "American Goldfinch").append("imageURL", "https://example.com/images/american-goldfinch.jpg")
        };  
        collection.insertMany(Arrays.asList(birds));
    }

    private void populateGroups(MongoCollection<Document> collection) {
        Document[] groups = {
            new Document("_id", group1)
                .append("name", "DFW birders")
                .append("ownerId", user1)
                .append("members", Arrays.asList(user1, user2, user3))
                .append("requests", Arrays.asList()),

            new Document("_id", group2)
                .append("name", "Coastal Bird Committee")
                .append("ownerId", user2)
                .append("members", Arrays.asList(user2, user1, user3))
                .append("requests", Arrays.asList(user4))
        };
        collection.insertMany(Arrays.asList(groups));
    }

    private void populatePosts(MongoCollection<Document> collection) {
        Document[] posts = {
            new Document("_id", post1)
                .append("userId", user1)
                .append("header", "Rare Tufted Titmouse spotting!")
                .append("tags", new Document("location", "San Antonio, TX"))
                .append("bird", bird7)
                .append("flagged", false)
                .append("group", group1)
                .append("help", false)
                .append("likes", Arrays.asList(user2))
                .append("image", "TODO")
                .append("textBody", "Found this Tufted Titmouse, let me know what you guys think!")
                .append("timestamp", new Date())
                .append("comments", Arrays.asList(
                    new Document("userId", user2)
                        .append("textBody", "Great shot!")
                        .append("timestamp", new Date()),
                    new Document("userId", user1)
                        .append("textBody", "Awesome bird! My favorite!")
                        .append("timestamp", new Date())
                )),

            new Document("_id", post2)
                .append("userId", user2)
                .append("header", "Downy Woodpecker at the park")
                .append("tags", new Document("location", "Richardson, TX"))
                .append("bird", bird6)
                .append("flagged", false)
                .append("group", group2)
                .append("help", false)
                .append("likes", Arrays.asList(user4, user3, user1))
                .append("image", "TODO")
                .append("textBody", "Found this Downy Woodpecker at the park!")
                .append("timestamp", new Date())
                .append("comments", Arrays.asList(
                    new Document("userId", user4)
                        .append("textBody", "One of my favorite parks to go spotting")
                        .append("timestamp", new Date()),
                    new Document("userId", user3)
                        .append("textBody", "Great work!")
                        .append("timestamp", new Date())
                ))
        };
        collection.insertMany(Arrays.asList(posts));
    }

    private void populateUsers(MongoCollection<Document> collection) {
        Document[] users = {
            new Document("_id", user1)
                .append("username", "birdwatcher_beth")
                .append("password", "Password1!")
                .append("profilePic", "backend/profile_pictures/default_pfp.jpg")
                .append("friends", Arrays.asList(user2, user3))
                .append("posts", Arrays.asList(post1))
                .append("groups", Arrays.asList(group1)),

            new Document("_id", user2)
                .append("username", "nature_nancy")
                .append("password", "Nature123!")
                .append("profilePic", "backend/profile_pictures/default_pfp.jpg")
                .append("friends", Arrays.asList(user1, user4))
                .append("posts", Arrays.asList(post2))
                .append("groups", Arrays.asList(group1, group2)),

            new Document("_id", user3)
                .append("username", "ornithology_oscar")
                .append("password", "Oscar!234")
                .append("profilePic", "backend/profile_pictures/default_pfp.jpg")
                .append("friends", Arrays.asList(user1))
                .append("posts", Arrays.asList())
                .append("groups", Arrays.asList(group1, group2)),

            new Document("_id", user4)
                .append("username", "spotter_scott")
                .append("password", "Scott#12")
                .append("profilePic", "backend/profile_pictures/default_pfp.jpg")
                .append("friends", Arrays.asList(user2))
                .append("posts", Arrays.asList())
                .append("groups", Arrays.asList())
        };
        collection.insertMany(Arrays.asList(users));
    }
}
