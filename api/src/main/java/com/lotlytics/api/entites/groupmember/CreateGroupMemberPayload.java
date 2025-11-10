package com.lotlytics.api.entites.groupmember;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the payload used to create a new GroupMember.
 */
@AllArgsConstructor
@Getter
public class CreateGroupMemberPayload {
    /** The ID of the user to add as a group member. Cannot be null. */
    @NotNull(message = "User ID cannot be null")
    private Integer userId;
    
    /** The ID of the role to assign to the user. Cannot be null. */
    @NotNull(message = "Role ID cannot be null")
    private Integer roleId;
}