package com.birdbook.controller;

import com.birdbook.models.PostUser;
import com.birdbook.service.PostUserService;
import org.springframework.web.bind.annotation.*;

import com.birdbook.models.Group;
import com.birdbook.models.User;
import com.birdbook.service.GroupService;

import jakarta.validation.Valid;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
@RestController
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;
    private final PostUserService puService;

    public GroupController(GroupService groupService, PostUserService puService) {
        this.groupService = groupService;
        this.puService = puService;
    }

    @GetMapping
    public List<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{id}")
    public Group getGroup(@PathVariable String id) {
        
        ObjectId groupObjId = new ObjectId(id);
        return groupService.getGroupById(groupObjId);
    }

    @GetMapping("/{groupId}/join-requests")
    public List<PostUser> getRequests(@PathVariable String groupId) {
        return groupService.getRequestedUsers(new ObjectId(groupId));
    }

    @GetMapping("/{groupId}/members")
    public List<PostUser> getMembers(@PathVariable String groupId) {
        return groupService.getGroupMembers(new ObjectId(groupId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_USER') or hasRole('SUPER_USER')")
    public ResponseEntity<String> createGroup(@RequestParam String name, @RequestParam String ownerId) {
        PostUser owner = puService.buildPostUser(new ObjectId(ownerId));
        groupService.createGroup(name, owner);
        return ResponseEntity.status(HttpStatus.CREATED).body("Group created successfully");
    }

    @PostMapping("/{groupId}/join-requests")
    @PreAuthorize("hasRole('ADMIN_USER') or hasRole('SUPER_USER')")
    public ResponseEntity<String> requestToJoin(
            @PathVariable String groupId,
            @RequestParam String userId
    ) {
        PostUser user = puService.buildPostUser(new ObjectId(userId));
        groupService.userRequestToJoin(user, new ObjectId(groupId));
        return ResponseEntity.ok("Join request sent");
    }

    @PutMapping("/{groupId}/join-requests/{userId}/approve")
    @PreAuthorize("hasRole('ADMIN_USER') or hasRole('SUPER_USER')")
    public ResponseEntity<String> approveJoin(
            @PathVariable String groupId,
            @PathVariable String userId
    ) {
        PostUser user = puService.buildPostUser(new ObjectId(userId));
        groupService.approveJoinRequest(user, new ObjectId(groupId));
        return ResponseEntity.ok("Join request approved");
    }

    @PutMapping("/{groupId}/join-requests/{userId}/deny")
    @PreAuthorize("hasRole('ADMIN_USER') or hasRole('SUPER_USER')")
    public ResponseEntity<String> denyJoinRequest(@PathVariable String groupId, @PathVariable String userId) {

        ObjectId groupObjId = new ObjectId(groupId);
        ObjectId userObjId = new ObjectId(userId);
        groupService.denyJoinRequest(puService.buildPostUser(userObjId), groupObjId);

        return new ResponseEntity<String>("Join request denied successfully", HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_USER') or hasRole('SUPER_USER')")
    public ResponseEntity<Group> updateGroup(@PathVariable String id, @RequestBody Group groupRequest) {

        ObjectId groupObjId = new ObjectId(id);
        Group updatedGroup = groupService.updateGroup(groupObjId, groupRequest);

        return ResponseEntity.ok(updatedGroup);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_USER') or hasRole('SUPER_USER')")
    public ResponseEntity<String> deleteGroup(@PathVariable String id) {

        ObjectId groupObjId = new ObjectId(id);
        groupService.deleteGroup(groupObjId);

        return new ResponseEntity<String>("Group deleted successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> leaveGroup(@PathVariable String groupId, @PathVariable String userId) {
        
        ObjectId groupObjId = new ObjectId(groupId);
        ObjectId userObjId = new ObjectId(userId);
        groupService.removeGroupMember(userObjId, groupObjId);
        return ResponseEntity.ok("Left group successfully");
    }
}