package com.birdbook.GroupTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.birdbook.models.Group;
import com.birdbook.models.PostUser;
import com.birdbook.repository.GroupDAO;
import com.birdbook.service.GroupService;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {
    
    @Mock
    private GroupDAO groupDAO;

    @InjectMocks
    private GroupService groupService;

    private Group testGroup;
    private PostUser testOwner;
    private PostUser testMember;
    private ObjectId groupId;

    @BeforeEach
    void setup(){
        groupId = new ObjectId();
        testOwner = new PostUser(new ObjectId(), "owner");
        testMember = new PostUser(new ObjectId(), "member");
        
        testGroup = new Group("Test Group", testOwner);
        testGroup.setId(groupId);
    }

    @Test
    public void getAllGroups_Success(){
        when(groupDAO.findAll()).thenReturn(List.of(testGroup));

        List<Group> result = groupService.getAllGroups();

        assertEquals(1, result.size());
        verify(groupDAO, times(1)).findAll();
    }

    @Test
    public void getGroupById_Success(){
        when(groupDAO.findById(groupId)).thenReturn(Optional.of(testGroup));

        Group result = groupService.getGroupById(groupId);

        assertEquals("Test Group", result.getName());
        verify(groupDAO, times(1)).findById(groupId);
    }

    @Test
    public void getGroupById_NotFound(){
        when(groupDAO.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> groupService.getGroupById(groupId));
    }

    @Test
    public void createGroup_Success(){
        groupService.createGroup("Test Group", testOwner);
        verify(groupDAO, times(1)).insert(any(Group.class));
    }

    @Test
    public void updateGroup_Success(){
        Group updatedGroup = new Group("Updated Name", testOwner);

        when(groupDAO.findById(groupId)).thenReturn(Optional.of(testGroup));
        when(groupDAO.save(any(Group.class))).thenReturn(testGroup);

        Group result = groupService.updateGroup(groupId, updatedGroup);

        verify(groupDAO, times(1)).save(testGroup);
        assertEquals("Updated Name", result.getName());
    }

    @Test
    public void deleteGroup_Success(){
        when(groupDAO.existsById(groupId)).thenReturn(true);

        groupService.deleteGroup(groupId);

        verify(groupDAO, times(1)).deleteById(groupId);
    }

    @Test
    public void deleteGroup_NotFound(){
        when(groupDAO.existsById(groupId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> groupService.deleteGroup(groupId));
        verify(groupDAO, times(0)).deleteById(groupId);
    }

    @Test
    public void userRequestToJoin_Success(){
        when(groupDAO.findById(groupId)).thenReturn(Optional.of(testGroup));
        when(groupDAO.save(any(Group.class))).thenReturn(testGroup);

        groupService.userRequestToJoin(testMember, groupId);

        verify(groupDAO, times(1)).save(testGroup);
        assertEquals(1, testGroup.getRequests().size());
    }

    @Test
    public void userRequestToJoin_AlreadyRequested(){
        testGroup.getRequests().add(testMember);

        when(groupDAO.findById(groupId)).thenReturn(Optional.of(testGroup));
    
        assertThrows(IllegalArgumentException.class, () -> groupService.userRequestToJoin(testMember, groupId));

        verify(groupDAO, never()).save(any(Group.class));
    }

    @Test
    public void userRequestToJoin_AlreadyMember(){
        testGroup.getMembers().add(testMember);

        when(groupDAO.findById(groupId)).thenReturn(Optional.of(testGroup));
    
        assertThrows(IllegalArgumentException.class, () -> groupService.userRequestToJoin(testMember, groupId));

        verify(groupDAO, never()).save(any(Group.class));
    }

    @Test
    public void approveJoinRequest_Success(){
        testGroup.getRequests().add(testMember);

        when(groupDAO.findById(groupId)).thenReturn(Optional.of(testGroup));
        when(groupDAO.save(any(Group.class))).thenReturn(testGroup);

        groupService.approveJoinRequest(testMember, groupId);

        verify(groupDAO, times(1)).save(testGroup);
        assertEquals(0, testGroup.getRequests().size());
        assertEquals(1, testGroup.getMembers().size());
    }

    @Test
    public void approveJoinRequest_NoRequest(){
        when(groupDAO.findById(groupId)).thenReturn(Optional.of(testGroup));

        assertThrows(IllegalArgumentException.class, () -> groupService.approveJoinRequest(testMember, groupId));
    }

    @Test
    public void denyJoinRequest_Success(){
        testGroup.getRequests().add(testMember);

        when(groupDAO.findById(groupId)).thenReturn(Optional.of(testGroup));
        when(groupDAO.save(any(Group.class))).thenReturn(testGroup);

        groupService.denyJoinRequest(testMember, groupId);

        verify(groupDAO, times(1)).save(testGroup);
        assertEquals(0, testGroup.getRequests().size());
    }

    @Test
    public void removeGroupMember_Success(){
        testGroup.getMembers().add(testMember);

        when(groupDAO.findById(groupId)).thenReturn(Optional.of(testGroup));
        when(groupDAO.save(any(Group.class))).thenReturn(testGroup);

        groupService.removeGroupMember(testMember.getUserId(), groupId);

        verify(groupDAO, times(1)).save(testGroup);
        assertEquals(0, testGroup.getMembers().size());
    }

    @Test
    public void removeGroupMember_NotMember(){
        when(groupDAO.findById(groupId)).thenReturn(Optional.of(testGroup));

        assertThrows(IllegalArgumentException.class, () -> groupService.removeGroupMember(testMember.getUserId(), groupId));
    }
}