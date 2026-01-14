package com.birdbook.GroupTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

import com.birdbook.models.Group;
import com.birdbook.repository.GroupDAO;
import com.birdbook.repository.UserDAO;
import com.birdbook.service.GroupService;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {
    
    @Mock
    private GroupDAO groupDAO;

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private GroupService groupService;

    @Test
    public void getAllGroups_Success(){
        groupService.getAllGroups();
        verify(groupDAO, times(1)).findAll();
    }

    @Test
    public void createGroup_Success(){
        groupService.createGroup("Test Group", null);
        verify(groupDAO, times(1)).insert(any(Group.class));
    }

    @Test
    public void updateGroup_Success(){

        ObjectId id = new ObjectId();

        Group existingGroup = new Group("Old Group Name", null);
        Group updatedGroup = new Group("Test Group Name", null);

        when(groupDAO.findById(id)).thenReturn(Optional.of(existingGroup));
        when(groupDAO.save(any(Group.class))).thenReturn(existingGroup);

        Group result = groupService.updateGroup(id, updatedGroup);

        verify(groupDAO, times(1)).save(existingGroup);
        assertEquals("Test Group Name", result.getName());
    }

    @Test
    public void deleteGroup_Success(){

        ObjectId id = new ObjectId();

        when(groupDAO.existsById(id)).thenReturn(true);

        groupService.deleteGroup(id);

        verify(groupDAO, times(1)).deleteById(id);
    }

    @Test
    public void userRequestToJoin_Success(){

        ObjectId groupId = new ObjectId();
        ObjectId userId = new ObjectId();

        Group existingGroup = new Group("Test Group", null);
        existingGroup.setRequests(new ObjectId[] {});

        when(groupDAO.findById(groupId)).thenReturn(Optional.of(existingGroup));
        when(groupDAO.save(any(Group.class))).thenReturn(existingGroup);

        groupService.userRequestToJoin(userId, groupId);

        verify(groupDAO, times(1)).save(existingGroup);
        assertEquals(1, existingGroup.getRequests().length);
        assertEquals(userId, existingGroup.getRequests()[0]);
    }

    @Test
    public void userRequestToJoin_AlreadyRequested(){

        ObjectId groupId = new ObjectId();
        ObjectId userId = new ObjectId();

        Group existingGroup = new Group("Test Group", null);
        existingGroup.setRequests(new ObjectId[] {userId});

        when(groupDAO.findById(groupId)).thenReturn(Optional.of(existingGroup));
    
        assertThrows(IllegalArgumentException.class, () -> groupService.userRequestToJoin(userId, groupId));

        verify(groupDAO, never()).save(any(Group.class));
    }

    @Test
    public void approveJoinRequest_Success(){

        ObjectId userId = new ObjectId();
        ObjectId groupId = new ObjectId();

        Group existingGroup = new Group("Test Group", null);
        existingGroup.setRequests(new ObjectId[] {userId});
        existingGroup.setMembers(new ObjectId[] {});

        when(groupDAO.findById(groupId)).thenReturn(Optional.of(existingGroup));
        when(groupDAO.save(any(Group.class))).thenReturn(existingGroup);

        groupService.approveJoinRequest(userId, groupId);
        verify(groupDAO, times(1)).save(existingGroup);
        assertEquals(0, existingGroup.getRequests().length);
        assertEquals(1, existingGroup.getMembers().length);
        assertEquals(userId, existingGroup.getMembers()[0]);
    }

    @Test
    public void denyJoinRequest_Success(){

        ObjectId userId = new ObjectId();
        ObjectId groupId = new ObjectId();

        Group existingGroup = new Group("Test Group", null);
        existingGroup.setRequests(new ObjectId[] {userId});

        when(groupDAO.findById(groupId)).thenReturn(Optional.of(existingGroup));
        when(groupDAO.save(any(Group.class))).thenReturn(existingGroup);

        groupService.denyJoinRequest(userId, groupId);
        verify(groupDAO, times(1)).save(existingGroup);
        assertEquals(0, existingGroup.getRequests().length);
    }
}
