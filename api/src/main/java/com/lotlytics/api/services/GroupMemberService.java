package com.lotlytics.api.services;

import java.util.List;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import com.lotlytics.api.entites.exceptions.BadRequestException;
import com.lotlytics.api.entites.exceptions.ConflictException;
import com.lotlytics.api.entites.exceptions.NotFoundException;
import com.lotlytics.api.entites.groupmember.CreateGroupMemberPayload;
import com.lotlytics.api.entites.groupmember.GroupMember;
import com.lotlytics.api.entites.groupmember.PutGroupMemberPayload;
import com.lotlytics.api.repositories.GroupMemberRepository;
import com.lotlytics.api.repositories.GroupRepository;
import com.lotlytics.api.repositories.RoleRepository;
import com.lotlytics.api.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * The GroupMemberService class defines service methods for managing group members.
 */
@Slf4j
@Service
public class GroupMemberService {
    
    GroupMemberRepository groupMemberRepository;
    GroupRepository groupRepository;
    UserRepository userRepository;
    RoleRepository roleRepository;
    
    /**
     * Constructs a GroupMemberService with the specified repositories.
     * 
     * @param groupMemberRepository the repository for group member operations
     * @param groupRepository the repository for group operations
     * @param userRepository the repository for user operations
     * @param roleRepository the repository for role operations
     */
    public GroupMemberService(
            GroupMemberRepository groupMemberRepository,
            GroupRepository groupRepository,
            UserRepository userRepository,
            RoleRepository roleRepository) {
        this.groupMemberRepository = groupMemberRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    
    /**
     * The createGroupMember method creates a new group member.
     * 
     * @param groupId the ID of the group
     * @param payload a CreateGroupMemberPayload that defines the new group member
     * @return The newly created group member
     * @throws NotFoundException if the group, user, or role does not exist
     * @throws ConflictException if the user is already a member of the group
     * @throws BadRequestException if the payload contains invalid data
     */
    public GroupMember createGroupMember(String groupId, CreateGroupMemberPayload payload) {
        Integer userId = payload.getUserId();
        Integer roleId = payload.getRoleId();
        
        // Check if group exists
        if (!groupRepository.existsById(groupId)) {
            throw new NotFoundException("Group with ID '" + groupId + "' does not exist");
        }
        
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with ID '" + userId + "' does not exist");
        }
        
        // Check if role exists
        if (!roleRepository.existsById(roleId)) {
            throw new NotFoundException("Role with ID '" + roleId + "' does not exist");
        }
        
        // Check if user is already a member of the group
        if (isGroupMember(groupId, userId)) {
            throw new ConflictException("User with ID '" + userId + "' is already a member of group '" + groupId + "'");
        }
        
        GroupMember groupMember = new GroupMember(groupId, userId, roleId);
        GroupMember out = groupMemberRepository.save(groupMember);
        log.info("Added user '" + userId + "' to group '" + groupId + "' with role '" + roleId + "'");
        return out;
    }
    
    /**
     * The putGroupMember method updates an existing group member's role.
     * 
     * @param groupId the ID of the group
     * @param userId the ID of the user whose role is being updated
     * @param payload a PutGroupMemberPayload that defines the updated role
     * @return The updated group member
     * @throws NotFoundException if the group, user, role, or group member does not exist
     * @throws BadRequestException if the payload contains invalid data
     */
    public GroupMember putGroupMember(String groupId, Integer userId, PutGroupMemberPayload payload) {
        Integer roleId = payload.getRoleId();
        
        // Check if group exists
        if (!groupRepository.existsById(groupId)) {
            throw new NotFoundException("Group with ID '" + groupId + "' does not exist");
        }
        
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with ID '" + userId + "' does not exist");
        }
        
        // Check if role exists
        if (!roleRepository.existsById(roleId)) {
            throw new NotFoundException("Role with ID '" + roleId + "' does not exist");
        }
        
        // Find the group member
        GroupMember groupMember = new GroupMember();
        groupMember.setGroupId(groupId);
        groupMember.setUserId(userId);
        List<GroupMember> members = groupMemberRepository.findAll(Example.of(groupMember));
        
        if (members.isEmpty()) {
            throw new NotFoundException("User with ID '" + userId + "' is not a member of group '" + groupId + "'");
        }
        
        // Update the role
        GroupMember existingMember = members.get(0);
        existingMember.setRoleId(roleId);
        GroupMember updatedMember = groupMemberRepository.save(existingMember);
        log.info("Updated role for user '" + userId + "' in group '" + groupId + "' to role '" + roleId + "'");
        return updatedMember;
    }
    
    /**
     * The getGroupMembers method gets all members of a group.
     * 
     * @param groupId the ID of the group
     * @return A list of all group members in the specified group
     * @throws NotFoundException if the group does not exist
     */
    public List<GroupMember> getGroupMembers(String groupId) {
        // Check if group exists
        if (!groupRepository.existsById(groupId)) {
            throw new NotFoundException("Group with ID '" + groupId + "' does not exist");
        }
        
        GroupMember groupMember = new GroupMember();
        groupMember.setGroupId(groupId);
        return groupMemberRepository.findAll(Example.of(groupMember));
    }
    
    /**
     * The isGroupMember method determines if a user is a member of a group.
     * 
     * @param groupId the ID of the group
     * @param userId the ID of the user
     * @return true if the user is a member of the group, false otherwise
     */
    public boolean isGroupMember(String groupId, Integer userId) {
        GroupMember groupMember = new GroupMember();
        groupMember.setGroupId(groupId);
        groupMember.setUserId(userId);
        return !groupMemberRepository.findAll(Example.of(groupMember)).isEmpty();
    }
    
    /**
     * The deleteGroupMember method removes a user from a group.
     * 
     * @param groupId the ID of the group
     * @param userId the ID of the user to remove from the group
     * @throws NotFoundException if the group, user, or group member does not exist
     */
    public void deleteGroupMember(String groupId, Integer userId) {
        // Check if group exists
        if (!groupRepository.existsById(groupId)) {
            throw new NotFoundException("Group with ID '" + groupId + "' does not exist");
        }
        
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with ID '" + userId + "' does not exist");
        }
        
        // Find the group member
        GroupMember groupMember = new GroupMember();
        groupMember.setGroupId(groupId);
        groupMember.setUserId(userId);
        List<GroupMember> members = groupMemberRepository.findAll(Example.of(groupMember));
        
        if (members.isEmpty()) {
            throw new NotFoundException("User with ID '" + userId + "' is not a member of group '" + groupId + "'");
        }
        
        // Delete the group member
        GroupMember memberToDelete = members.get(0);
        groupMemberRepository.deleteById(memberToDelete.getId());
        log.info("Removed user '" + userId + "' from group '" + groupId + "'");
    }
}