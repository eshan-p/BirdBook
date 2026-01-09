package com.birdbook.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.birdbook.models.Group;
import com.birdbook.repository.GroupDAO;

@Service
public class GroupService {
    private final GroupDAO groupDAO;

    public GroupService(GroupDAO groupDAO) {
        this.groupDAO = groupDAO;
    }

    public List<Group> getAllGroups() {
        return groupDAO.findAll();
    }
}
