package com.lotlytics.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lotlytics.api.entites.Message;

import lombok.extern.slf4j.Slf4j;

/*
 * The DefaultController is used for test endpoints.
 * A good way to determine if your application is running properly.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class DefaultController {

    /**
     * The index methods returns a heartwarming message, confirming your suspicions 
     * if you actually started your application up correctly.
     * 
     * @return ResponseEntity<Messgae>
     */

    @GetMapping
    public ResponseEntity<Message> index() {
        return new ResponseEntity<Message>(
            new Message("Welcome to the Lotlytics REST API!"),
            HttpStatus.OK
        );
    }
    
}
