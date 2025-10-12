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
import com.lotlytics.api.services.GroupService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
/*
 * The GroupController Class handles request and responses for
 * The /api/v1/group endpoint.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/group")
public class GroupController extends GenericController {

    GroupService groupService;
    private static String endpointMsg = "%s /api/v1/group%s";

    /**
     * The GroupController Class handles request and responses for
     * The /api/v1/group endpoint.
     * 
     * @see GroupService
     * 
     * @param groupService A GroupService bean providing service methods.
     */
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * The getGroup method handles the /api/v1/group?name=someVal endpoint.
     * 
     * @param name The name of the group
     * @return A list of Groups with the matching name.
     */
    @GetMapping(params = "name")
    public ResponseEntity<?> getGroup(@RequestParam String name) {
        log.info(String.format(endpointMsg, "GET", "?name="+name));
        return callServiceMethod(() -> groupService.getGroup(name), HttpStatus.OK);
    }

    /**
     * The getAllGroups method handles the /api/v1/group endpoint.
     * 
     * @return A list of all knoew groups.
     */
    @GetMapping
    public ResponseEntity<?> getAllGroups() {
        log.info(String.format(endpointMsg, "GET", ""));
        return callServiceMethod(() -> groupService.getAllGroups(), HttpStatus.OK);
    }


    /**
     * The createGroup method handles the /api/v1/group endpoint.
     * This method creates a new group using the GroupService.
     * 
     * @see CreateGroupPayload
     * 
     * @param payload The CreateGroupPayload for this request.
     * @return The new Group.
     */
    @PostMapping
    public ResponseEntity<?> createGroup(@Valid @RequestBody CreateGroupPayload payload) {
        log.info(String.format(endpointMsg, "POST", ""));
        return callServiceMethod(() -> groupService.createGroup(payload), HttpStatus.CREATED);
    }

    /**
     * The deleteGroup method handles the /api/v1/group?groupId=someVal endpoint.
     * The method throws a 404 if the groupId does not exist.
     * 
     * @param groupId The Id of the group the lot is apart of.
     * @return no content, confirming the group was removed.
     */
    @DeleteMapping(params = "groupId")
    public ResponseEntity<?> deleteGroup(@RequestParam String groupId) {
        log.info(String.format(endpointMsg, "DELETE", "?groupId="+groupId));
        return callVoidServiceMethod(() -> groupService.deleteGroup(groupId), HttpStatus.NO_CONTENT);
    }
}
