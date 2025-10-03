package com.lotlytics.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lotlytics.api.entites.Message;
import com.lotlytics.api.entites.event.CreateEventPayload;

@RestController
@RequestMapping("/api/v1/event")
public class EventController {
    
    @GetMapping(params = {"groupId", "lotId"})
    public ResponseEntity<Message> getLotEvents(@RequestParam String groupId, @RequestParam Integer lotId) {
        return new ResponseEntity<Message>(new Message("Events for group " + groupId + " and lot " + lotId), HttpStatus.OK);
    }

    @GetMapping(params = "groupId")
    public ResponseEntity<Message> getGroupEvents(@RequestParam String groupId) {
        return new ResponseEntity<Message>(new Message("Events for group " + groupId), HttpStatus.OK);
    }

    @GetMapping(params = {"groupId", "eventId"})
    public ResponseEntity<Message> getSpecificEvent(@RequestParam String groupId, @RequestParam Integer eventId) {
        return new ResponseEntity<Message>(new Message("Event " + eventId + " for group " + groupId), HttpStatus.OK);
    }

    @PostMapping(params = {"groupId", "lotId"})
    public ResponseEntity<Message> createLotEvent(@RequestParam String groupId, @RequestParam Integer lotId, @RequestBody CreateEventPayload payload) {
        return new ResponseEntity<Message>(new Message("this is the event creation endpoint"), HttpStatus.OK);
    }
}
