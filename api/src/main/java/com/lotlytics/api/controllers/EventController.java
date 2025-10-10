package com.lotlytics.api.controllers;

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
import jakarta.validation.Valid;
import com.lotlytics.api.entites.event.CreateEventPayload;
import com.lotlytics.api.entites.event.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/*
 * The EventController Class handles request and responses for
 * The /api/v1/event endpoint.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/event")
public class EventController {

    EventService eventService;
    LotService lotService;
    GroupService groupService;
    private static String endpointMsg = "%s /api/v1/event%s";

    /**
     * The EventController Class handles request and responses for
     * The /api/v1/event endpoint.
     * 
     * @see EventService
     * @see EventRepository
     * 
     * @param lotService A LotService bean providing service methods.
     * @param groupService A GroupService bean providing group methods.
     * @param eventService A EventService bean providing group methods.
     */
    public EventController(LotService lotService, GroupService groupService, EventService eventService) {
        this.eventService = eventService;
        this.lotService = lotService;
        this.groupService = groupService;
    }
    
    /**
     * The getLotEvents method handles the /api/v1/event?groupId=SomeVal&lotid=SomeVal endpoint.
     * The method throws 404s if the groupId or lotId does not exist.
     * 
     * @param groupId The Id of the group the lot is apart of.
     * @param lotId The Id of the lot.
     * @return A list of Events from the given lot in the given group.
     */
    @GetMapping(params = {"groupId", "lotId"})
    public ResponseEntity<?> getLotEvents(@RequestParam String groupId, @RequestParam Integer lotId) {
        log.info(String.format(endpointMsg, "GET", "?groupId="+groupId+"&lotId="+lotId));
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

    /**
     * The getGroupEvents method handles the /api/v1/event?groupId=someVal endpoint.
     * The method throws a 404 if the groupId does not exist.
     * 
     * @param groupId The Id of the group the lot is apart of.
     * @return A list of Events from the given lot in the given group.
     */
    @GetMapping(params = {"groupId"})
    public ResponseEntity<?> getGroupEvents(@RequestParam String groupId) {
        log.info(String.format(endpointMsg, "GET", "?groupId="+groupId));
        if (!groupService.isAGroup(groupId)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Group ID does not exist"));
        } else {
            return new ResponseEntity<List<Event>>(
                eventService.getGroupEvents(groupId),
                HttpStatus.OK
            );
        }
    }

    /**
     * The createLotEvent method handles the /api/v1/event?groupId=someVal&lotId=someVal endpoint.
     * The method throws a 404 if the groupId or lotId does not exist.
     * This method stores the Event using the EventService.
     * 
     * @see CreateEventPayload
     * 
     * @param groupId The Id of the group the lot is apart of.
     * @param lotId The Id of the lot this event is from.
     * @param payload The CreateEventPayload for this request.
     * @return The new created Event.
     */
    @PostMapping(params = {"groupId", "lotId"})
    public ResponseEntity<?> createLotEvent(@RequestParam String groupId, @RequestParam Integer lotId, @Valid @RequestBody CreateEventPayload payload) {
        log.info(String.format(endpointMsg, "POST", "?groupId="+groupId+"&lotId="+lotId));
        if (!groupService.isAGroup(groupId)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Group ID does not exist"));
        } else if (!lotService.isALot(lotId)){
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Lot does not exist"));
        } else {
            return new ResponseEntity<Event>(
                eventService.saveEvent(groupId, lotId, payload),
                HttpStatus.CREATED
            );
        }
    }
}
