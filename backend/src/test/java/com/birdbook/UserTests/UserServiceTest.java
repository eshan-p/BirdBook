package com.birdbook.UserTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.birdbook.models.User;
import com.birdbook.repository.UserDAO;
import com.birdbook.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    @Test
    public void getAllUsers_Success(){
        userService.getAllUsers();
        verify(userDAO, times(1)).findAll();
    }

    @Test
    public void getUserById_Success(){

        ObjectId id = new ObjectId();

        when(userDAO.findById(id)).thenReturn(Optional.of(new User("testuser", "password")));

        userService.getUserById(id);

        verify(userDAO, times(1)).findById(id);
    }

    @Test
    public void getUserById_UserNotFound(){

        ObjectId id = new ObjectId();

        when(userDAO.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(id));
        verify(userDAO, times(1)).findById(id);
    }

    @Test
    public void registerUser_Success(){

        String username = "newuser";
        String password = "pass123";

        when(userDAO.findByUsername(username)).thenReturn(Optional.empty());

        userService.registerUser(username, password);

        verify(userDAO, times(1)).insert(any(User.class));
    }

    @Test
    public void registerUser_UsernameTaken(){

        String username = "existinguser";
        String password = "pass123";

        when(userDAO.findByUsername(username)).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(username, password));
        verify(userDAO, times(0)).insert(any(User.class));
    }

    @Test
    public void updateUser_Success(){

        ObjectId id = new ObjectId();
        User existingUser = new User("olduser", "oldpass");
        User updatedData = new User("updateduser", "newpass123");

        when(userDAO.findById(id)).thenReturn(Optional.of(existingUser));
        when(userDAO.save(any(User.class))).thenReturn(existingUser);

        User result = userService.updateUser(id, updatedData);

        verify(userDAO, times(1)).save(existingUser);
        assertEquals("updateduser", result.getUsername());
        assertEquals("newpass123", result.getPassword());
    }

    @Test
    public void updateUser_UserNotFound(){

        ObjectId id = new ObjectId();
        User updatedData = new User("updateduser", "newpass123");

        when(userDAO.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(id, updatedData));
        verify(userDAO, times(0)).save(any(User.class));
    }

    @Test
    public void deleteUser_Success(){

        ObjectId id = new ObjectId();

        when(userDAO.existsById(id)).thenReturn(true);

        userService.deleteUser(id);

        verify(userDAO, times(1)).deleteById(id);
    }

    @Test
    public void deleteUser_UserNotFound(){

        ObjectId id = new ObjectId();

        when(userDAO.existsById(id)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(id));
        verify(userDAO, times(0)).deleteById(id);
    }

    @Test
    public void addFriend_Success(){
        ObjectId userId = new ObjectId();
        ObjectId friendId = new ObjectId();

        User user = new User("user1", "pass1");
        user.setFriends(new ObjectId[] {});
        User friend = new User("user2", "pass2");

        when(userDAO.findById(userId)).thenReturn(Optional.of(user));
        when(userDAO.findById(friendId)).thenReturn(Optional.of(friend));

        userService.addFriend(userId, friendId);

        verify(userDAO, times(1)).save(user);
    }

    @Test
    public void addFriend_FriendNotFound(){
        ObjectId userId = new ObjectId();
        ObjectId friendId = new ObjectId();

        User user = new User("user1", "pass1");
        user.setFriends(new ObjectId[] {});

        when(userDAO.findById(userId)).thenReturn(Optional.of(user));
        when(userDAO.findById(friendId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.addFriend(userId, friendId));
        verify(userDAO, times(0)).save(user);
    }
}