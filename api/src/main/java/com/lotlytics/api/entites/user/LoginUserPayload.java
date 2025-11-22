package com.lotlytics.api.entites.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * The LoginUserPayload represents a user logging in
 */
@AllArgsConstructor
@Getter
public class LoginUserPayload {
    /** The username of a user */
    private String username;

    /** The password of a user */
    private String password;
}
