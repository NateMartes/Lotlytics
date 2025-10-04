package com.lotlytics.api.controllers;

import java.util.List;

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
import com.lotlytics.api.entites.lot.PutLotPayload;
import com.lotlytics.api.entites.lot.Lot;
import com.lotlytics.api.services.LotService;

@RestController
/**
 * The standard API endpoint handler for the Lotlytics API /lot endpoints
 *
 * @author Nathaniel Martes
 * @version 1.0.0
 */
@RequestMapping("/api/v1/lot")
public class LotController {

    private LotService lotService;

    public LotController(LotService lotService) {
        this.lotService = lotService;
    }

    @GetMapping(params = {"groupId"})
    public ResponseEntity<List<Lot>> getAllLots(@RequestParam String groupId) {
        return new ResponseEntity<List<Lot>>(
            lotService.getLotsByGroup(groupId),
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
