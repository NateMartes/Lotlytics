package com.lotlytics.api.controllers;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lotlytics.api.entites.Message;
import com.lotlytics.api.entites.lot.CreateLotPayload;
import com.lotlytics.api.entites.lot.LotResponseList;
import com.lotlytics.api.entites.lot.PutLotPayload;

@RestController
/**
 * The standard API endpoint handler for the Lotlytics API /lot endpoints
 *
 * @author Nathaniel Martes
 * @version 1.0.0
 */
@RequestMapping("/api/v1/lot")
public class LotController {

    @GetMapping(params = {"groupId"})
    /**
     * Handles the /lot endpoint for the Lotlytics API at /api/v1/lot.
     * This gets all the lots for a specific group.
     *
     * @param groupId The required groupId to discover the lots that belong to that group.
     * @return      A ResponseEntity<Message> with the default API message.
     */
    public ResponseEntity<LotResponseList> getAllLots(@RequestParam String groupId) {
        // Return all Lots for the group
        return new ResponseEntity<LotResponseList>(
            new LotResponseList(new ArrayList<>()),
            HttpStatus.OK
        );
    }

    @GetMapping(params = {"groupId", "lotId"})
    public ResponseEntity<Message> getLot(@RequestParam String groupId, @RequestParam Integer lotId) {
        // Return the specifc Lots for the group
        return new ResponseEntity<Message>(
            new Message(groupId),
            HttpStatus.OK
        );
    
    }
    @PostMapping(params = {"groupId"})
    public ResponseEntity<Message> postLot(@RequestParam String groupId, @RequestBody CreateLotPayload payload) {
        // Add a lot
        return new ResponseEntity<Message>(
            new Message(groupId),
            HttpStatus.OK
        );
    }

    @PutMapping(params = {"groupId","lotId"})
    public ResponseEntity<Message> putLot(@RequestParam String groupId, @RequestParam Integer lotId, @RequestBody PutLotPayload payload) {
        // Update a lot
        return new ResponseEntity<Message>(
            new Message(groupId),
            HttpStatus.OK
        );
    }

    @DeleteMapping(params = {"groupId", "lotId"})
    public ResponseEntity<Message> deleteLot(@RequestParam String groupId, @RequestParam Integer lotId) {
        // Delete a specifc lot
        return new ResponseEntity<Message>(
            new Message(groupId),
            HttpStatus.OK
        );
    }

    @DeleteMapping(params = {"groupId"})
    public ResponseEntity<Message> deleteAllLosts(@RequestParam String groupId) {
        // Delete all lots for a group
        return new ResponseEntity<Message>(
            new Message(groupId),
            HttpStatus.OK
        );
    }

}
