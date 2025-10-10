package com.lotlytics.api.entites.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import jakarta.validation.constraints.Size;

/*
 * Represents the payload used to create a new Group.
 */
@AllArgsConstructor
@Getter
public class CreateGroupPayload {

    /** 
     * The name of the group.
     * Must not exceed 246 characters.
     */
    @Size(max = 246, message = "Group names cannot be greater than 246 characters")
    String name;
}
