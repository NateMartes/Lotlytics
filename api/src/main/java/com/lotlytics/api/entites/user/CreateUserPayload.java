package com.lotlytics.api.entites.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * The CreateUserPayload represents a new user being created
 */
@AllArgsConstructor
@Getter
public class CreateUserPayload {
    /** The username of a user */
    @Size(max = 255, min = 6, message = "Usernames cannot be greater than 255 and cannot be less than 6 characters")
    private String username;

    /** The email of a user */
    @Email
    private String email;

    /** The password of a user */
    @Size(max = 255, message = "Lot names cannot be greater than 255 characters")
    private String password;
}
