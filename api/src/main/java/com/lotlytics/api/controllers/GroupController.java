package com.lotlytics.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.lotlytics.api.entites.group.CreateGroupPayload;
import com.lotlytics.api.entites.group.Group;
import com.lotlytics.api.services.GroupService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/group")

public class GroupController {

    GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<?> createGroup(@Valid @RequestBody CreateGroupPayload payload) {
            return new ResponseEntity<Group>(
                groupService.createGroup(payload),
                HttpStatus.OK
            );
    }
}
