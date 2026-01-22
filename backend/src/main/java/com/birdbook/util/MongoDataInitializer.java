package com.birdbook.util;

import java.util.*;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.birdbook.models.Role;
import com.mongodb.client.MongoCollection;

@Component
public class MongoDataInitializer implements CommandLineRunner {

    // ===== USERS =====
    private final ObjectId adminUser = new ObjectId();
    private final ObjectId superUser = new ObjectId();
    private final List<ObjectId> basicUsers = new ArrayList<>();

    // ===== GROUPS =====
    private final ObjectId groupDFW = new ObjectId();
    private final ObjectId groupCoastal = new ObjectId();

    // ===== BIRDS =====
    private final List<ObjectId> birds = Arrays.asList(
            new ObjectId(), new ObjectId(), new ObjectId(), new ObjectId(),
            new ObjectId(), new ObjectId(), new ObjectId(), new ObjectId()
    );

    // ===== POSTS =====
    private final List<ObjectId> posts = new ArrayList<>();

    @Override
    public void run(String... args) {
        MongoCollection<Document> db;

        db = ConnectionHandler.getDatabase().getCollection("birds");
        if (db.countDocuments() == 0) populateBirds(db);

        db = ConnectionHandler.getDatabase().getCollection("users");
        if (db.countDocuments() == 0) populateUsers(db);

        db = ConnectionHandler.getDatabase().getCollection("groups");
        if (db.countDocuments() == 0) populateGroups(db);

        db = ConnectionHandler.getDatabase().getCollection("posts");
        if (db.countDocuments() == 0) populatePosts(db);
    }

    private void populateBirds(MongoCollection<Document> collection) {
        String[] names = {
                "American Robin", "Northern Cardinal", "Black-capped Chickadee",
                "Blue Jay", "House Finch", "Downy Woodpecker",
                "Tufted Titmouse", "American Goldfinch"
        };

        List<Document> docs = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            docs.add(new Document("_id", birds.get(i))
                    .append("commonName", names[i])
                    .append("imageURL", "https://example.com/birds/" + i + ".jpg"));
        }
        collection.insertMany(docs);
    }

    private void populateUsers(MongoCollection<Document> collection) {
        List<Document> docs = new ArrayList<>();

        docs.add(userDoc(adminUser, "admin_alice", "Admin1!", Role.ADMIN_USER));
        docs.add(userDoc(superUser, "super_sam", "Super1!", Role.SUPER_USER));

        for (int i = 0; i < 25; i++) {
            ObjectId id = new ObjectId();
            basicUsers.add(id);
            docs.add(userDoc(id, "birder" + i, "Bird1!", Role.BASIC_USER));
        }

        collection.insertMany(docs);
    }

    private Document userDoc(ObjectId id, String username, String password, Role role) {
        return new Document("_id", id)
                .append("username", username)
                .append("password", password)
                .append("role", role.name())
                .append("profilePic", "backend_profile_pictures/default_pfp.jpg")
                .append("friends", new ArrayList<>())
                .append("posts", new ArrayList<>())
                .append("groups", new ArrayList<>());
    }

    private void populateGroups(MongoCollection<Document> collection) {
        collection.insertMany(Arrays.asList(
                new Document("_id", groupDFW)
                        .append("name", "DFW Birders")
                        .append("ownerId", adminUser)
                        .append("members", basicUsers)
                        .append("requests", List.of()),

                new Document("_id", groupCoastal)
                        .append("name", "Coastal Bird Committee")
                        .append("ownerId", superUser)
                        .append("members", basicUsers.subList(0, 10))
                        .append("requests", List.of())
        ));
    }

    private void populatePosts(MongoCollection<Document> collection) {
        Random rand = new Random();
        List<Document> docs = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            ObjectId postId = new ObjectId();
            posts.add(postId);

            ObjectId author = basicUsers.get(rand.nextInt(basicUsers.size()));
            ObjectId bird = birds.get(rand.nextInt(birds.size()));

            docs.add(new Document("_id", postId)
                    .append("userId", author)
                    .append("header", "Bird Sighting #" + i)
                    .append("tags", new Document("location",
                            new Document("latitude", 32.5 + rand.nextDouble())
                                    .append("longitude", -96.5 + rand.nextDouble())
                    ))
                    .append("bird", bird)
                    .append("flagged", false)
                    .append("group", rand.nextBoolean() ? groupDFW : groupCoastal)
                    .append("help", rand.nextBoolean())
                    .append("likes", List.of())
                    .append("image", "TODO")
                    .append("textBody", "Automated test post content " + i)
                    .append("timestamp", new Date(System.currentTimeMillis() - rand.nextInt(1_000_000_000)))
                    .append("comments", generateComments(rand))
            );
        }
        collection.insertMany(docs);
    }

    private List<Document> generateComments(Random rand) {
        int count = rand.nextInt(5);
        List<Document> comments = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            comments.add(new Document("userId",
                    basicUsers.get(rand.nextInt(basicUsers.size())))
                    .append("textBody", "Nice spotting! " + i)
                    .append("timestamp", new Date()));
        }
        return comments;
    }
}
