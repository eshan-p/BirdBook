package com.birdbook.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.birdbook.models.Group;
import com.birdbook.models.User;
import com.birdbook.service.GroupService;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/all")
    public List<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{id}")
    public Group getGroup(@PathVariable String groupId) {
        
        ObjectId groupObjId = new ObjectId(groupId);
        return groupService.getGroupById(groupObjId);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createGroup(@RequestBody Group groupRequest) {

        groupService.createGroup(groupRequest.getName(), groupRequest.getId());

        return new ResponseEntity<String>("Group created successfully", HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable String groupId, @RequestBody Group groupRequest) {

        ObjectId groupObjId = new ObjectId(groupId);
        Group updatedGroup = groupService.updateGroup(groupObjId, groupRequest);

        return ResponseEntity.ok(updatedGroup);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteGroup(@PathVariable String groupId) {

        ObjectId groupObjId = new ObjectId(groupId);
        groupService.deleteGroup(groupObjId);

        return new ResponseEntity<String>("Group deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/getAllRequests/{groupId}")
    public ResponseEntity<List<User>> getAllRequests(@PathVariable String groupId) {

        ObjectId groupObjId = new ObjectId(groupId);
        List<User> requestedUsers = groupService.getRequestedUsers(groupObjId);
        
        return ResponseEntity.ok(requestedUsers);
    }

    @GetMapping("/getAllMembers/{groupId}")
    public ResponseEntity<List<User>> getAllMembers(@PathVariable String groupId) {

        ObjectId groupObjId = new ObjectId(groupId);
        List<User> members = groupService.getGroupMembers(groupObjId);
        
        return ResponseEntity.ok(members);
    }

    @PostMapping("/requestToJoin/{groupId}/{userId}")
    public ResponseEntity<String> userRequestToJoin(@PathVariable String groupId, @PathVariable String userId) {
        
        ObjectId groupObjId = new ObjectId(groupId);
        ObjectId userObjId = new ObjectId(userId);
        groupService.userRequestToJoin(userObjId, groupObjId);

        return new ResponseEntity<String>("Join request sent successfully", HttpStatus.OK);
    }

    @PutMapping("/approve/{groupId}/{userId}")
    public ResponseEntity<String> approveJoinRequest(@PathVariable String groupId, @PathVariable String userId) {

        ObjectId groupObjId = new ObjectId(groupId);
        ObjectId userObjId = new ObjectId(userId);
        groupService.approveJoinRequest(userObjId, groupObjId);

        return new ResponseEntity<String>("Join request approved successfully", HttpStatus.OK);
    }

    @PutMapping("/deny/{groupId}/{userId}")
    public ResponseEntity<String> denyJoinRequest(@PathVariable String groupId, @PathVariable String userId) {

        ObjectId groupObjId = new ObjectId(groupId);
        ObjectId userObjId = new ObjectId(userId);
        groupService.denyJoinRequest(userObjId, groupObjId);

        return new ResponseEntity<String>("Join request denied successfully", HttpStatus.OK);
    }
}