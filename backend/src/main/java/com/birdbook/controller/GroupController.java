package com.birdbook.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.birdbook.models.Group;
import com.birdbook.service.GroupService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;


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
    
}
