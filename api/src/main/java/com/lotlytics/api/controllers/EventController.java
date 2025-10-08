package com.lotlytics.api.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lotlytics.api.services.EventService;
import com.lotlytics.api.services.GroupService;
import com.lotlytics.api.services.LotService;
import com.lotlytics.api.entites.event.Event;

@RestController
@RequestMapping("/api/v1/event")
public class EventController {

    EventService eventService;
    LotService lotService;
    GroupService groupService;

    public EventController(LotService lotService, GroupService groupService, EventService eventService) {
        this.eventService = eventService;
        this.lotService = lotService;
        this.groupService = groupService;
    }
    
    @GetMapping(params = {"groupId", "lotId"})
    public ResponseEntity<?> getLotEvents(@RequestParam String groupId, @RequestParam Integer lotId) {
        if (!groupService.isAGroup(groupId)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Group ID does not exist"));
        } else if (!lotService.isALot(lotId)){
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Lot does not exist"));
        } else {
            return new ResponseEntity<List<Event>>(
                eventService.getLotEvents(groupId, lotId),
                HttpStatus.OK
            );
        }
    }
/*
    @GetMapping(params = "groupId")
    public ResponseEntity<?> getGroupEvents(@RequestParam String groupId) {
        return new ResponseEntity<Message>(new Message("Events for group " + groupId), HttpStatus.OK);
    }

    @GetMapping(params = {"groupId", "eventId"})
    public ResponseEntity<?> getEvent(@RequestParam String groupId, @RequestParam Integer eventId) {
        return new ResponseEntity<Message>(new Message("Event " + eventId + " for group " + groupId), HttpStatus.OK);
    }

    @PostMapping(params = {"groupId", "lotId"})
    public ResponseEntity<?> createLotEvent(@RequestParam String groupId, @RequestParam Integer lotId, @RequestBody CreateEventPayload payload) {
        return new ResponseEntity<Message>(new Message("this is the event creation endpoint"), HttpStatus.OK);
    }
*/
}
