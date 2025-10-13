package com.lotlytics.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lotlytics.api.entites.user.CreateUserPayload;
import com.lotlytics.api.services.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/*
 * The UserController Class handles request and responses for
 * The /api/v1/user endpoint.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController extends GenericController {
    
    private UserService userService;
    private static String endpointMsg = "%s /api/v1/user%s";

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserPayload payload) {
        log.info(String.format(endpointMsg, "POST", ""));
        return callServiceMethod(() -> userService.createUser(payload), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        log.info(String.format(endpointMsg, "GET", ""));
        return callServiceMethod(() -> userService.getAllUsers(), HttpStatus.OK);
    }
}
