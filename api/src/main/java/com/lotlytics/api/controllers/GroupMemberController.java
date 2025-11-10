package com.lotlytics.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import com.lotlytics.api.entites.groupmember.CreateGroupMemberPayload;
import com.lotlytics.api.entites.groupmember.PutGroupMemberPayload;
import com.lotlytics.api.services.GroupMemberService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * The GroupMemberController class handles requests and responses for
 * the /api/v1/group/member endpoint.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/group/member")
public class GroupMemberController extends GenericController {
    
    GroupMemberService groupMemberService;
    private static String endpointMsg = "%s /api/v1/group/member%s";
    
    /**
     * The GroupMemberController class handles requests and responses for
     * the /api/v1/group/member endpoint.
     * 
     * @see GroupMemberService
     * 
     * @param groupMemberService A GroupMemberService bean providing service methods
     */
    public GroupMemberController(GroupMemberService groupMemberService) {
        this.groupMemberService = groupMemberService;
    }
    
    /**
     * The getGroupMembers method handles the /api/v1/group/member?groupId=someVal endpoint.
     * 
     * @param groupId The ID of the group
     * @return A list of all members in the specified group
     */
    @GetMapping(params = "groupId")
    public ResponseEntity<?> getGroupMembers(@RequestParam String groupId) {
        log.info(String.format(endpointMsg, "GET", "?groupId=" + groupId));
        return callServiceMethod(() -> groupMemberService.getGroupMembers(groupId), HttpStatus.OK);
    }
    
    /**
     * The createGroupMember method handles the /api/v1/group/member?groupId=someVal endpoint.
     * This method creates a new group member using the GroupMemberService.
     * 
     * @see CreateGroupMemberPayload
     * 
     * @param groupId The ID of the group
     * @param payload The CreateGroupMemberPayload for this request
     * @return The new group member
     */
    @PostMapping(params = "groupId")
    public ResponseEntity<?> createGroupMember(
            @RequestParam String groupId,
            @Valid @RequestBody CreateGroupMemberPayload payload) {
        log.info(String.format(endpointMsg, "POST", "?groupId=" + groupId));
        return callServiceMethod(() -> groupMemberService.createGroupMember(groupId, payload), HttpStatus.CREATED);
    }
    
    /**
     * The putGroupMember method handles the /api/v1/group/member?groupId=someVal&userId=someVal endpoint.
     * This method updates an existing group member's role using the GroupMemberService.
     * 
     * @see PutGroupMemberPayload
     * 
     * @param groupId The ID of the group
     * @param userId The ID of the user whose role is being updated
     * @param payload The PutGroupMemberPayload for this request
     * @return The updated group member
     */
    @PutMapping(params = {"groupId", "userId"})
    public ResponseEntity<?> putGroupMember(
            @RequestParam String groupId,
            @RequestParam Integer userId,
            @Valid @RequestBody PutGroupMemberPayload payload) {
        log.info(String.format(endpointMsg, "PUT", "?groupId=" + groupId + "&userId=" + userId));
        return callServiceMethod(() -> groupMemberService.putGroupMember(groupId, userId, payload), HttpStatus.OK);
    }
    
    /**
     * The deleteGroupMember method handles the /api/v1/group/member?groupId=someVal&userId=someVal endpoint.
     * This method removes a user from a group using the GroupMemberService.
     * 
     * @param groupId The ID of the group
     * @param userId The ID of the user to remove from the group
     * @return no content, confirming the user was removed from the group
     */
    @DeleteMapping(params = {"groupId", "userId"})
    public ResponseEntity<?> deleteGroupMember(
            @RequestParam String groupId,
            @RequestParam Integer userId) {
        log.info(String.format(endpointMsg, "DELETE", "?groupId=" + groupId + "&userId=" + userId));
        return callVoidServiceMethod(() -> groupMemberService.deleteGroupMember(groupId, userId), HttpStatus.NO_CONTENT);
    }
}