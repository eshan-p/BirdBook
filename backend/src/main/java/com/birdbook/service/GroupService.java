package com.birdbook.service;

import java.util.ArrayList;
import java.util.List;
import com.birdbook.repository.UserDAO;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.birdbook.models.Group;
import com.birdbook.models.User;
import com.birdbook.repository.GroupDAO;

@Service
public class GroupService {

    private final UserDAO userDAO;
    private final GroupDAO groupDAO;

    public GroupService(GroupDAO groupDAO, UserDAO userDAO) {
        this.groupDAO = groupDAO;
        this.userDAO = userDAO;
    }

    public List<Group> getAllGroups() {
        return groupDAO.findAll();
    }

    public Group getGroupById(ObjectId groupId) {
        return groupDAO.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Group not found."));
    }

    public void createGroup(String groupName, ObjectId creatorId) {
        Group newGroup = new Group(groupName, creatorId);
        groupDAO.insert(newGroup);
    }

    public Group updateGroup(ObjectId groupId, Group updatedData) {
        Group existingGroup = groupDAO.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found."));

        existingGroup.setName(updatedData.getName());

        return groupDAO.save(existingGroup);
    }

    public void deleteGroup(ObjectId groupId) {
        if (!groupDAO.existsById(groupId)) {
            throw new IllegalArgumentException("Group not found.");
        }

        groupDAO.deleteById(groupId);
    }


    public List<User> getRequestedUsers(ObjectId groupId){
        Group group = groupDAO.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found."));
        
        ObjectId[] requestIds = group.getRequests();
        return userDAO.findAllById(List.of(requestIds));
    }


    public List<User> getGroupMembers(ObjectId groupId){
        Group group = groupDAO.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found."));

        ObjectId[] memberIds = group.getMembers();
        return userDAO.findAllById(List.of(memberIds));
    }


    public void userRequestToJoin(ObjectId userId, ObjectId groupId) {
        Group group = groupDAO.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found."));

        List<ObjectId> requests = new ArrayList<>(List.of(group.getRequests()));
        if (requests.contains(userId)){
            throw new IllegalArgumentException("Request already sent.");
        }
        else if (List.of(group.getMembers()).contains(userId)){
            throw new IllegalArgumentException("User is already a member of the group.");
        }

        requests.add(userId);
        group.setRequests(requests.toArray(new ObjectId[0]));
        groupDAO.save(group);
    }


    public void approveJoinRequest(ObjectId userId, ObjectId groupId) {
        Group group = groupDAO.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found."));

        List<ObjectId> requests = new ArrayList<>(List.of(group.getRequests()));
        if (!requests.contains(userId)){
            throw new IllegalArgumentException("No join request from this user.");
        }

        requests.remove(userId);
        group.setRequests(requests.toArray(new ObjectId[0]));

        List<ObjectId> members = new ArrayList<>(List.of(group.getMembers()));
        members.add(userId);
        group.setMembers(members.toArray(new ObjectId[0]));

        groupDAO.save(group);
    }


    public void denyJoinRequest(ObjectId userId, ObjectId groupId) {
        Group group = groupDAO.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found."));

        List<ObjectId> requests = new ArrayList<>(List.of(group.getRequests()));
        if (!requests.contains(userId)){
            throw new IllegalArgumentException("No join request from this user.");
        }

        requests.remove(userId);
        group.setRequests(requests.toArray(new ObjectId[0]));

        groupDAO.save(group);
    }
}
