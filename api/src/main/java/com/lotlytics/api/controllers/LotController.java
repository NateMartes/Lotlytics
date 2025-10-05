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

import com.lotlytics.api.entites.lot.CreateLotPayload;
import com.lotlytics.api.entites.lot.PutLotPayload;
import com.lotlytics.api.entites.lot.Lot;
import com.lotlytics.api.services.GroupService;
import com.lotlytics.api.services.LotService;

import java.util.Map;

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
    private GroupService groupService;

    public LotController(LotService lotService, GroupService groupService) {
        this.lotService = lotService;
        this.groupService = groupService;
    }

    @GetMapping(params = {"groupId"})
    public ResponseEntity<?> getAllLots(@RequestParam String groupId) {
        if (!groupService.isAGroup(groupId)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Group ID does not exist"));
        }
        return new ResponseEntity<List<Lot>>(
            lotService.getLotsByGroup(groupId),
            HttpStatus.OK
        );
    }

    @GetMapping(params = {"groupId", "lotId"})
    public ResponseEntity<?> getLot(@RequestParam String groupId, @RequestParam Integer lotId) {
        try {
            return new ResponseEntity<Lot>(
                lotService.getLot(groupId, lotId),
                HttpStatus.OK
                );
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Lot does not exist"));
        }
    }    
    @PostMapping(params = {"groupId"})
    public ResponseEntity<?> postLot(@RequestParam String groupId, @RequestBody CreateLotPayload payload) {
        if (!groupService.isAGroup(groupId)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Group ID does not exist"));
        } else {
            return new ResponseEntity<Lot>(
                lotService.postLot(groupId, payload),
                HttpStatus.OK
            );
        }
    }

    @PutMapping(params = {"groupId","lotId"})
    public ResponseEntity<?> putLot(@RequestParam String groupId, @RequestParam Integer lotId, @RequestBody PutLotPayload payload) {
        if (!groupService.isAGroup(groupId)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Group ID does not exist"));
        } else if (!lotService.isALot(lotId)){
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Lot does not exist"));
        } else {
            return new ResponseEntity<Lot>(
                lotService.putLot(groupId, lotId, payload),
                HttpStatus.OK
            );
        }
    }

    @DeleteMapping(params = {"groupId", "lotId"})
    public ResponseEntity<?> deleteLot(@RequestParam String groupId, @RequestParam Integer lotId) {
        if (!groupService.isAGroup(groupId)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Group ID does not exist"));
        } else if (!lotService.isALot(lotId)){
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Lot does not exist"));
        } else {
            lotService.deleteLot(groupId, lotId);
            return ResponseEntity.noContent().build();    
        }
    }

    @DeleteMapping(params = {"groupId"})
    public ResponseEntity<?> deleteAllLosts(@RequestParam String groupId) {
        if (!groupService.isAGroup(groupId)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Group ID does not exist"));
        } else {
            lotService.deleteAllLosts(groupId);
            return ResponseEntity.noContent().build();    
        }
    }

}
