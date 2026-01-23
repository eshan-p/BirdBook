package com.birdbook.util;

import java.util.*;

import com.birdbook.models.Role;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoCollection;

@Component
public class MongoDataInitializer implements CommandLineRunner {

    // ===== USERS =====
    private final ObjectId adminUser = new ObjectId();
    private final ObjectId superUser = new ObjectId();
    private final List<ObjectId> basicUsers = new ArrayList<>();
    private final Map<ObjectId, String> userNames = new HashMap<>();

    // ===== GROUPS =====
    private final ObjectId groupDFW = new ObjectId();
    private final ObjectId groupCoastal = new ObjectId();

    // ===== BIRDS =====
    private final List<ObjectId> birds = List.of(
            new ObjectId(), new ObjectId(), new ObjectId(), new ObjectId(),
            new ObjectId(), new ObjectId(), new ObjectId(), new ObjectId()
    );

    // ===== POSTS =====
    private final List<ObjectId> posts = new ArrayList<>();

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

    // =====================================================
    // USERS
    // =====================================================

    private void populateUsers(MongoCollection<Document> collection) {
        List<Document> docs = new ArrayList<>();

        userNames.put(adminUser, "admin_alice");
        docs.add(userDoc(adminUser, "admin_alice", "Admin1!", Role.ADMIN_USER));

        userNames.put(superUser, "super_sam");
        docs.add(userDoc(superUser, "super_sam", "Super1!", Role.SUPER_USER));

        for (int i = 0; i < 25; i++) {
            ObjectId id = new ObjectId();
            String username = "birder" + i;

            basicUsers.add(id);
            userNames.put(id, username);

            docs.add(userDoc(id, username, "Bird1!", Role.BASIC_USER));
        }

        collection.insertMany(docs);
    }

    private Document userDoc(ObjectId id, String username, String password, Role role) {
        return new Document("_id", id)
                .append("username", username)
                .append("password", password)
                .append("role", role.name())
                .append("profilePic", "backend_profile_pictures/default_pfp.jpg")
                .append("friends", List.of())
                .append("posts", List.of())
                .append("groups", List.of());
    }

    // =====================================================
    // GROUPS
    // =====================================================

    private void populateGroups(MongoCollection<Document> collection) {
        collection.insertMany(List.of(
                new Document("_id", groupDFW)
                        .append("name", "DFW Birders")
                        .append("owner", postUser(adminUser))
                        .append("members", basicUsers.stream().map(this::postUser).toList())
                        .append("requests", List.of()),

                new Document("_id", groupCoastal)
                        .append("name", "Coastal Bird Committee")
                        .append("owner", postUser(superUser))
                        .append("members", basicUsers.subList(0, 10).stream().map(this::postUser).toList())
                        .append("requests", List.of())
        ));
    }

    // =====================================================
    // BIRDS
    // =====================================================

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

    // =====================================================
    // POSTS + COMMENTS
    // =====================================================

    private void populatePosts(MongoCollection<Document> collection) {
        Random rand = new Random();
        List<Document> docs = new ArrayList<>();

        long baseTime = System.currentTimeMillis() - 2_000_000_000L;

        for (int i = 0; i < 100; i++) {
            ObjectId postId = new ObjectId();
            posts.add(postId);

            ObjectId authorId = basicUsers.get(rand.nextInt(basicUsers.size()));
            ObjectId bird = birds.get(rand.nextInt(birds.size()));

            docs.add(new Document("_id", postId)
                    .append("user", postUser(authorId))
                    .append("header", "Bird Sighting #" + i)
                    .append("bird", bird)
                    .append("group", rand.nextBoolean() ? groupDFW : groupCoastal)
                    .append("flagged", false)
                    .append("help", rand.nextBoolean())
                    .append("likes", List.of())
                    .append("image", "TODO")
                    .append("textBody", "Automated test post content " + i)
                    .append("timestamp", new Date(baseTime + (i * 10_000)))
                    .append("comments", generateComments(rand))
            );
        }

        collection.insertMany(docs);
    }

    private List<Document> generateComments(Random rand) {
        int count = rand.nextInt(5);
        List<Document> comments = new ArrayList<>();

        long baseTime = System.currentTimeMillis() - 500_000;

        for (int i = 0; i < count; i++) {
            ObjectId uid = basicUsers.get(rand.nextInt(basicUsers.size()));

            comments.add(new Document("user", postUser(uid))
                    .append("textBody", "Nice spotting! " + i)
                    .append("timestamp", new Date(baseTime + (i * 5_000)))
            );
        }

        return comments;
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private Document postUser(ObjectId id) {
        return new Document("userId", id)
                .append("username", userNames.get(id));
    }
}
