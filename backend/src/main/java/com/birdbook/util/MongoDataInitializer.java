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
    private final List<ObjectId> birds = new ArrayList<>();

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
        // Array of [commonName, scientificName]
        String[][] birdData = {
            // Waterfowl
            {"Mallard", "Anas platyrhynchos"},
            {"Canada Goose", "Branta canadensis"},
            {"Wood Duck", "Aix sponsa"},
            {"American Black Duck", "Anas rubripes"},
            {"Northern Pintail", "Anas acuta"},
            {"Green-winged Teal", "Anas crecca"},
            {"Blue-winged Teal", "Spatula discors"},
            {"Gadwall", "Mareca strepera"},
            {"American Wigeon", "Mareca americana"},
            {"Northern Shoveler", "Spatula clypeata"},
            {"Canvasback", "Aythya valisineria"},
            {"Redhead", "Aythya americana"},
            {"Ring-necked Duck", "Aythya collaris"},
            {"Greater Scaup", "Aythya marila"},
            {"Lesser Scaup", "Aythya affinis"},
            {"Bufflehead", "Bucephala albeola"},
            {"Common Goldeneye", "Bucephala clangula"},
            {"Hooded Merganser", "Lophodytes cucullatus"},
            {"Common Merganser", "Mergus merganser"},
            {"Red-breasted Merganser", "Mergus serrator"},
            
            // Upland Game Birds
            {"Wild Turkey", "Meleagris gallopavo"},
            {"Ring-necked Pheasant", "Phasianus colchicus"},
            {"Ruffed Grouse", "Bonasa umbellus"},
            {"Northern Bobwhite", "Colinus virginianus"},
            {"Scaled Quail", "Callipepla squamata"},
            {"California Quail", "Callipepla californica"},
            {"Gambel's Quail", "Callipepla gambelii"},
            {"Mourning Dove", "Zenaida macroura"},
            {"Rock Pigeon", "Columba livia"},
            {"Eurasian Collared-Dove", "Streptopelia decaocto"},
            
            // Herons & Egrets
            {"Great Blue Heron", "Ardea herodias"},
            {"Great Egret", "Ardea alba"},
            {"Snowy Egret", "Egretta thula"},
            {"Little Blue Heron", "Egretta caerulea"},
            {"Tricolored Heron", "Egretta tricolor"},
            {"Cattle Egret", "Bubulcus ibis"},
            {"Green Heron", "Butorides virescens"},
            {"Black-crowned Night-Heron", "Nycticorax nycticorax"},
            {"Yellow-crowned Night-Heron", "Nyctanassa violacea"},
            
            // Raptors
            {"Turkey Vulture", "Cathartes aura"},
            {"Black Vulture", "Coragyps atratus"},
            {"Osprey", "Pandion haliaetus"},
            {"Bald Eagle", "Haliaeetus leucocephalus"},
            {"Northern Harrier", "Circus hudsonius"},
            {"Sharp-shinned Hawk", "Accipiter striatus"},
            {"Cooper's Hawk", "Accipiter cooperii"},
            {"Red-shouldered Hawk", "Buteo lineatus"},
            {"Broad-winged Hawk", "Buteo platypterus"},
            {"Red-tailed Hawk", "Buteo jamaicensis"},
            {"Rough-legged Hawk", "Buteo lagopus"},
            {"Golden Eagle", "Aquila chrysaetos"},
            {"American Kestrel", "Falco sparverius"},
            {"Merlin", "Falco columbarius"},
            {"Peregrine Falcon", "Falco peregrinus"},
            
            // Shorebirds
            {"Killdeer", "Charadrius vociferus"},
            {"American Avocet", "Recurvirostra americana"},
            {"Black-necked Stilt", "Himantopus mexicanus"},
            {"Spotted Sandpiper", "Actitis macularius"},
            {"Greater Yellowlegs", "Tringa melanoleuca"},
            {"Lesser Yellowlegs", "Tringa flavipes"},
            {"Willet", "Tringa semipalmata"},
            {"Sanderling", "Calidris alba"},
            {"Dunlin", "Calidris alpina"},
            {"Least Sandpiper", "Calidris minutilla"},
            
            // Gulls & Terns
            {"Ring-billed Gull", "Larus delawarensis"},
            {"Herring Gull", "Larus argentatus"},
            {"Great Black-backed Gull", "Larus marinus"},
            {"Laughing Gull", "Leucophaeus atricilla"},
            {"Bonaparte's Gull", "Chroicocephalus philadelphia"},
            {"Caspian Tern", "Hydroprogne caspia"},
            {"Common Tern", "Sterna hirundo"},
            {"Forster's Tern", "Sterna forsteri"},
            {"Least Tern", "Sternula antillarum"},
            {"Black Tern", "Chlidonias niger"},
            
            // Owls
            {"Great Horned Owl", "Bubo virginianus"},
            {"Eastern Screech-Owl", "Megascops asio"},
            {"Western Screech-Owl", "Megascops kennicottii"},
            {"Barred Owl", "Strix varia"},
            {"Barn Owl", "Tyto alba"},
            {"Long-eared Owl", "Asio otus"},
            {"Short-eared Owl", "Asio flammeus"},
            {"Northern Saw-whet Owl", "Aegolius acadicus"},
            {"Burrowing Owl", "Athene cunicularia"},
            
            // Woodpeckers
            {"Red-headed Woodpecker", "Melanerpes erythrocephalus"},
            {"Red-bellied Woodpecker", "Melanerpes carolinus"},
            {"Downy Woodpecker", "Dryobates pubescens"},
            {"Hairy Woodpecker", "Dryobates villosus"},
            {"Pileated Woodpecker", "Dryocopus pileatus"},
            {"Northern Flicker", "Colaptes auratus"},
            {"Yellow-bellied Sapsucker", "Sphyrapicus varius"},
            {"Acorn Woodpecker", "Melanerpes formicivorus"},
            
            // Flycatchers
            {"Eastern Phoebe", "Sayornis phoebe"},
            {"Say's Phoebe", "Sayornis saya"},
            {"Great Crested Flycatcher", "Myiarchus crinitus"},
            {"Western Kingbird", "Tyrannus verticalis"},
            {"Eastern Kingbird", "Tyrannus tyrannus"},
            {"Scissor-tailed Flycatcher", "Tyrannus forficatus"},
            {"Willow Flycatcher", "Empidonax traillii"},
            {"Least Flycatcher", "Empidonax minimus"},
            
            // Vireos
            {"White-eyed Vireo", "Vireo griseus"},
            {"Blue-headed Vireo", "Vireo solitarius"},
            {"Red-eyed Vireo", "Vireo olivaceus"},
            {"Warbling Vireo", "Vireo gilvus"},
            
            // Jays & Crows
            {"Blue Jay", "Cyanocitta cristata"},
            {"Steller's Jay", "Cyanocitta stelleri"},
            {"Western Scrub-Jay", "Aphelocoma californica"},
            {"American Crow", "Corvus brachyrhynchos"},
            {"Common Raven", "Corvus corax"},
            {"Fish Crow", "Corvus ossifragus"},
            {"Black-billed Magpie", "Pica hudsonia"},
            
            // Chickadees & Titmice
            {"Black-capped Chickadee", "Poecile atricapillus"},
            {"Carolina Chickadee", "Poecile carolinensis"},
            {"Mountain Chickadee", "Poecile gambeli"},
            {"Chestnut-backed Chickadee", "Poecile rufescens"},
            {"Tufted Titmouse", "Baeolophus bicolor"},
            {"Oak Titmouse", "Baeolophus inornatus"},
            {"Juniper Titmouse", "Baeolophus ridgwayi"},
            
            // Nuthatches & Creepers
            {"White-breasted Nuthatch", "Sitta carolinensis"},
            {"Red-breasted Nuthatch", "Sitta canadensis"},
            {"Brown-headed Nuthatch", "Sitta pusilla"},
            {"Pygmy Nuthatch", "Sitta pygmaea"},
            {"Brown Creeper", "Certhia americana"},
            
            // Wrens
            {"Carolina Wren", "Thryothorus ludovicianus"},
            {"House Wren", "Troglodytes aedon"},
            {"Winter Wren", "Troglodytes hiemalis"},
            {"Marsh Wren", "Cistothorus palustris"},
            {"Bewick's Wren", "Thryomanes bewickii"},
            {"Cactus Wren", "Campylorhynchus brunneicapillus"},
            {"Rock Wren", "Salpinctes obsoletus"},
            {"Canyon Wren", "Catherpes mexicanus"},
            
            // Thrushes
            {"Eastern Bluebird", "Sialia sialis"},
            {"Western Bluebird", "Sialia mexicana"},
            {"Mountain Bluebird", "Sialia currucoides"},
            {"Townsend's Solitaire", "Myadestes townsendi"},
            {"Veery", "Catharus fuscescens"},
            {"Hermit Thrush", "Catharus guttatus"},
            {"Wood Thrush", "Hylocichla mustelina"},
            {"American Robin", "Turdus migratorius"},
            {"Varied Thrush", "Ixoreus naevius"},
            
            // Mockingbirds & Thrashers
            {"Gray Catbird", "Dumetella carolinensis"},
            {"Northern Mockingbird", "Mimus polyglottos"},
            {"Brown Thrasher", "Toxostoma rufum"},
            {"Curve-billed Thrasher", "Toxostoma curvirostre"},
            
            // Starlings & Waxwings
            {"European Starling", "Sturnus vulgaris"},
            {"Cedar Waxwing", "Bombycilla cedrorum"},
            {"Bohemian Waxwing", "Bombycilla garrulus"},
            
            // Warblers
            {"Yellow Warbler", "Setophaga petechia"},
            {"Yellow-rumped Warbler", "Setophaga coronata"},
            {"Black-and-white Warbler", "Mniotilta varia"},
            {"American Redstart", "Setophaga ruticilla"},
            {"Common Yellowthroat", "Geothlypis trichas"},
            {"Prothonotary Warbler", "Protonotaria citrea"},
            {"Pine Warbler", "Setophaga pinus"},
            {"Palm Warbler", "Setophaga palmarum"},
            {"Blackpoll Warbler", "Setophaga striata"},
            {"Black-throated Green Warbler", "Setophaga virens"},
            {"Orange-crowned Warbler", "Leiothlypis celata"},
            {"Nashville Warbler", "Leiothlypis ruficapilla"},
            {"Tennessee Warbler", "Leiothlypis peregrina"},
            {"Northern Parula", "Setophaga americana"},
            {"Magnolia Warbler", "Setophaga magnolia"},
            {"Chestnut-sided Warbler", "Setophaga pensylvanica"},
            
            // Sparrows
            {"Eastern Towhee", "Pipilo erythrophthalmus"},
            {"Spotted Towhee", "Pipilo maculatus"},
            {"Chipping Sparrow", "Spizella passerina"},
            {"Field Sparrow", "Spizella pusilla"},
            {"American Tree Sparrow", "Spizelloides arborea"},
            {"Fox Sparrow", "Passerella iliaca"},
            {"Song Sparrow", "Melospiza melodia"},
            {"Lincoln's Sparrow", "Melospiza lincolnii"},
            {"Swamp Sparrow", "Melospiza georgiana"},
            {"White-throated Sparrow", "Zonotrichia albicollis"},
            {"White-crowned Sparrow", "Zonotrichia leucophrys"},
            {"Dark-eyed Junco", "Junco hyemalis"},
            {"Savannah Sparrow", "Passerculus sandwichensis"},
            {"Grasshopper Sparrow", "Ammodramus savannarum"},
            {"Lark Sparrow", "Chondestes grammacus"},
            {"Vesper Sparrow", "Pooecetes gramineus"},
            
            // Cardinals & Allies
            {"Northern Cardinal", "Cardinalis cardinalis"},
            {"Pyrrhuloxia", "Cardinalis sinuatus"},
            {"Rose-breasted Grosbeak", "Pheucticus ludovicianus"},
            {"Black-headed Grosbeak", "Pheucticus melanocephalus"},
            {"Blue Grosbeak", "Passerina caerulea"},
            {"Indigo Bunting", "Passerina cyanea"},
            {"Painted Bunting", "Passerina ciris"},
            {"Dickcissel", "Spiza americana"},
            
            // Blackbirds & Orioles
            {"Red-winged Blackbird", "Agelaius phoeniceus"},
            {"Eastern Meadowlark", "Sturnella magna"},
            {"Western Meadowlark", "Sturnella neglecta"},
            {"Yellow-headed Blackbird", "Xanthocephalus xanthocephalus"},
            {"Brewer's Blackbird", "Euphagus cyanocephalus"},
            {"Common Grackle", "Quiscalus quiscula"},
            {"Great-tailed Grackle", "Quiscalus mexicanus"},
            {"Brown-headed Cowbird", "Molothrus ater"},
            {"Orchard Oriole", "Icterus spurius"},
            {"Baltimore Oriole", "Icterus galbula"},
            {"Bullock's Oriole", "Icterus bullockii"},
            
            // Finches
            {"House Finch", "Haemorhous mexicanus"},
            {"Purple Finch", "Haemorhous purpureus"},
            {"Cassin's Finch", "Haemorhous cassinii"},
            {"American Goldfinch", "Spinus tristis"},
            {"Lesser Goldfinch", "Spinus psaltria"},
            {"Pine Siskin", "Spinus pinus"},
            {"Evening Grosbeak", "Coccothraustes vespertinus"},
            {"Common Redpoll", "Acanthis flammea"},
            
            // Old World Sparrows
            {"House Sparrow", "Passer domesticus"}
        };

        List<Document> docs = new ArrayList<>();
        
        for (int i = 0; i < birdData.length; i++) {
            ObjectId birdId = new ObjectId();
            this.birds.add(birdId);
            
            String commonName = birdData[i][0];
            String scientificName = birdData[i][1];
            
            docs.add(new Document("_id", birdId)
                    .append("commonName", commonName)
                    .append("scientificName", scientificName)
                    .append("imageURL", "https://example.com/birds/" + 
                        commonName.toLowerCase().replace(" ", "-").replace("'", "") + ".jpg"));
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
        return new Document("id", id)
                .append("username", userNames.get(id));
    }
}
