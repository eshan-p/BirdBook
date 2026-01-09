package PostTests;
import com.birdbook.models.Comment;
import com.birdbook.models.Post;
import com.birdbook.repository.PostDAO;
import com.birdbook.service.PostService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTests {
    @Mock
    private PostDAO postDAO;

    @InjectMocks
    private PostService postService;

    private Post testPost;
    private ObjectId postId;
    private ObjectId birdId;
    private ObjectId groupId;

    @BeforeEach
    void setup(){
        //MockitoAnnotations.openMocks(this);

        postId = new ObjectId();
        birdId = new ObjectId();
        groupId = new ObjectId();

        //setup test pokemon model
        testPost = new Post();
        testPost.setId(postId);
        testPost.setHeader("Staraptor");
        testPost.setTextBody("HE MEGA EVOLVED IN FRONT OF ME");

        testPost.setFlagged(false);
        testPost.setHelp(false);
        testPost.setImage("ImagePathForMegaStaraptor");

        //setting up testing tags
        Map<String, String> tags = new HashMap<>();
        tags.put("location", "Lumiose");
        testPost.setTags(tags);

        testPost.setBird(birdId);
        testPost.setGroup(groupId);

        //might add better data here later
        testPost.setLikes(new ArrayList<>());
        testPost.setComments(new ArrayList<>());

        //testPost.setTimestamp("NULL");
    }

    //get by id
    @Test
    void getPostById_Found_ReturnsPost()  {
        when(postDAO.findById(postId)).thenReturn(Optional.of(testPost));

        Optional<Post> result = postService.getPostById(postId);

        assertTrue(result.isPresent());
        assertEquals("Staraptor", result.get().getHeader());
        verify(postDAO).findById(postId);
    }

    @Test
    void getPostById_NotFound_ReturnsEmpty() {
        when(postDAO.findById(postId)).thenReturn(Optional.empty());

        Optional<Post> result = postService.getPostById(postId);

        assertTrue(result.isEmpty());
        verify(postDAO).findById(postId);
    }

    //update posts
    @Test
    void updatePost_HeaderOnly_DoesNotDeleteComments() {
        Comment comment = new Comment(new ObjectId(), "Nice bird!");
        testPost.setComments(List.of(comment));

        Post update = new Post();
        update.setHeader("Updated Header");

        when(postDAO.findById(postId)).thenReturn(Optional.of(testPost));
        when(postDAO.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        Post result = postService.updatePost(postId, update);

        assertEquals("Updated Header", result.getHeader());
        assertEquals(1, result.getComments().size());
        assertEquals("Nice bird!", result.getComments().get(0).getTextBody());
    }

    @Test
    void updatePost_UpdatesTags() {
        Post update = new Post();

        Map<String, String> newTags = new HashMap<>();
        newTags.put("location", "San Antonio, TX");
        update.setTags(newTags);

        when(postDAO.findById(postId)).thenReturn(Optional.of(testPost));
        when(postDAO.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        Post result = postService.updatePost(postId, update);

        assertEquals("San Antonio, TX", result.getTags().get("location"));
    }

    @Test
    void updatePost_PostNotFound_ThrowsException() {
        when(postDAO.findById(postId)).thenReturn(Optional.empty());

        Post update = new Post();

        assertThrows(RuntimeException.class,
                () -> postService.updatePost(postId, update));
    }

    //get all posts
    @Test
    void getAllPosts_Success_ReturnsPosts(){
        when(postDAO.findAll()).thenReturn(List.of(testPost));

        List<Post> result = postService.getAllPosts();

        assertEquals(1,result.size());
    }

    @Test
    void getAllPosts_Empty_ReturnsEmpty(){
        when(postDAO.findAll()).thenReturn(new ArrayList<>());

        List<Post> result = postService.getAllPosts();

        assertTrue(result.isEmpty());
    }

    //get all friends posts
    @Test
    void getAllPostsGivenFriendIds_Success_ReturnsPosts(){
        ArrayList friendList = new ArrayList<ObjectId>();
        //NOTE TO CHANGE THIS LATERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
        friendList.add(postId);

        when(postDAO.findAllByFriends()).thenReturn(List.of(testPost));

        List<Post> result = postService.findAllByFriends();

        assertEquals(1,result.size());
    }

    @Test
    void getAllPostsGivenFriendsIds_Empty_ReturnsEmpty(){

    }

    //create post and return postid

    //Retrieve list of posts by tags

    //return post info from id
}
