package com.lotlytics.api.entites.groupmember;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

/**
 * Represents the payload used to update an existing GroupMember.
 * Only the role ID can be updated.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PutGroupMemberPayload {

    /** The ID of the role to assign to the user. Cannot be null. */
    @NotNull(message = "Role ID cannot be null")
    private Integer roleId;
}

