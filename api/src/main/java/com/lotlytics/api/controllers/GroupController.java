package com.lotlytics.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.lotlytics.api.entites.group.CreateGroupPayload;
import com.lotlytics.api.entites.group.Group;
import com.lotlytics.api.services.GroupService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/group")

public class GroupController {

    GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public ResponseEntity<?> getGroup(@RequestParam String name) {
            return new ResponseEntity<List<Group>>(
                groupService.getGroup(name),
                HttpStatus.OK
            );
    }

    @PostMapping
    public ResponseEntity<?> createGroup(@Valid @RequestBody CreateGroupPayload payload) {
            return new ResponseEntity<Group>(
                groupService.createGroup(payload),
                HttpStatus.OK
            );
    }

    @DeleteMapping
    public ResponseEntity<?> deleteGroup(@RequestParam String groupId) {
        if (!groupService.isAGroup(groupId)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Group ID does not exist"));
        } else {
            groupService.deleteGroup(groupId);
            return ResponseEntity.noContent().build();    
        }
    }
}
