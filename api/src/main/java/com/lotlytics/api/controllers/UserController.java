package com.lotlytics.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lotlytics.api.entites.ErrorMessage;
import com.lotlytics.api.entites.user.CreateUserPayload;
import com.lotlytics.api.entites.user.LoginUserPayload;
import com.lotlytics.api.services.UserService;

import java.util.Map;
import java.util.HashMap;

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

    /**
    * The UserController Class handles request and responses for
    * The /api/v1/user endpoint.
    * @param userServuce a UserService bean
    */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * The createUser method handles the /api/v1/user endpoint.
     * The method throws a 409 if the user exists.
     * 
     * @param payload a CreateUserPayload.
     * @return The newly created user.
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserPayload payload) {
        log.info(String.format(endpointMsg, "POST", ""));
        return callServiceMethod(() -> userService.createUser(payload), HttpStatus.CREATED);
    }

    /**
     * The getAllUsers method handles the /api/v1/user endpoint.
     * 
     * @return A list of all users.
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        log.info(String.format(endpointMsg, "GET", ""));
        return callServiceMethod(() -> userService.getAllUsers(), HttpStatus.OK);
    }

    /**
     * The getUser method handles the /api/v1/user endpoint.
     * The method throws a 404 if the user does not exist.
     * 
     * @param username the username of a user.
     * @return A User.
     */
    @GetMapping(params = "username")
    public ResponseEntity<?> getUser(@Valid @RequestParam String username) {
        log.info(String.format(endpointMsg, "GET", "?username=" + username));
        return callServiceMethod(() -> userService.getUserByUsername(username), HttpStatus.OK);
    }

    /**
     * The loginUser method handles the /api/v1/user/login endpoint.
     * The method throws a 404 if the user does not exist.
     * 
     * @param username the username of a user.
     * @return A User.
     */
    @PostMapping(path = "/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginUserPayload payload) {
        log.info(String.format(endpointMsg, "POST", "/login"));

        // We still wrap this response in the callServiceMethod to handle for errors
        ResponseEntity<?> out = callServiceMethod(() -> userService.loginUser(payload), HttpStatus.OK);
        Object token = out.getBody();
        if (out.getStatusCode() != HttpStatus.OK || token == null) {
            return out;
        }

        String tokenString = token.toString();
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", tokenString).httpOnly(true).secure(false).path("/").build();
        return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).build();
    }

    /**
     * The getCurrentUser method handles the /api/v1/user/me endpoint.
     * The method throws a 401 or 403 if the user is not logged in.
     * 
     * @param username the username of a user.
     * @return A User.
     */
    @GetMapping(path = "/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        log.info(String.format(endpointMsg, "GET", "/me"));

        // If the token is valid, then userDetails will not be null and we can sign the user in
        if (userDetails == null) {
            return new ResponseEntity<ErrorMessage>(HttpStatus.FORBIDDEN);
        }

        Map<String, String> payload = new HashMap<String, String>();
        payload.put("username", userDetails.getUsername());
        return new ResponseEntity<Map<String, String>>(payload, HttpStatus.OK);
    }
}
