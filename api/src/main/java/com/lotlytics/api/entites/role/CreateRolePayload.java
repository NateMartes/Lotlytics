package com.lotlytics.api.entites.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the payload used to create a new Role.
 */
@AllArgsConstructor
@Getter
public class CreateRolePayload {
    /** The name of the role. Maximum 255 characters and cannot be blank. */
    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 255, message = "Role name cannot be greater than 255 characters")
    private String name;
}