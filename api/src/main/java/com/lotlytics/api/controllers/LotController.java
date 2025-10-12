package com.lotlytics.api.controllers;

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
import com.lotlytics.api.services.LotService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/*
 * The LotController Class handles request and responses for
 * The /api/v1/lot endpoint.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/lot")
public class LotController extends GenericController {

    private LotService lotService;
    private static String endpointMsg = "%s /api/v1/lot%s";

    /**
     * The LotController Class handles request and responses for
     * The /api/v1/lot endpoint.
     * 
     * @see LotService
     * 
     * @param lotService A LotService bean providing service methods.
     * @param groupService A GroupService bean providing service methods.
     */
    public LotController(LotService lotService) {
        this.lotService = lotService;
    }

    /**
     * The getGroup method handles the /api/v1/lot?groupId=someVal endpoint.
     * The method throws a 404 if the groupId does not exist.
     * 
     * @param groupId The Id of the group the lot belongs to.
     * @return A list of Lots that belong to this group.
     */
    @GetMapping(params = {"groupId"})
    public ResponseEntity<?> getAllLots(@RequestParam String groupId) {
        log.info(String.format(endpointMsg, "GET", "?groupId="+groupId));
        return callServiceMethod(() -> lotService.getLotsByGroup(groupId), HttpStatus.OK);
    }

    /**
     * The getLot method handles the /api/v1/lot?groupId=someVal&lotId=someVal endpoint.
     * The method throws a 404 if the groupId or lotId does not exist.
     * The method throws a 403 if the lot does not belong to the group.
     * 
     * @param groupId The Id of the group the lot belongs to.
     * @param lotId The Id of this lot.
     * @return The specific lot requested.
     */
    @GetMapping(params = {"groupId", "lotId"})
    public ResponseEntity<?> getLot(@RequestParam String groupId, @RequestParam Integer lotId) {
        log.info(String.format(endpointMsg, "GET", "?groupId="+groupId+"&lotId="+lotId));
        return callServiceMethod(() -> lotService.getLot(groupId, lotId), HttpStatus.OK);
    }   
    
    /**
     * The postLot method handles the /api/v1/lot?groupId=someVal endpoint.
     * The method throws a 404 if the groupId does not exist.
     * 
     * @see CreateLotPayload
     * 
     * @param groupId The Id of the group the lot belongs to.
     * @param payload The CreateLotPayload for this request.
     * @return The created lot.
     */
    @PostMapping(params = {"groupId"})
    public ResponseEntity<?> postLot(@RequestParam String groupId, @Valid @RequestBody CreateLotPayload payload) {
        log.info(String.format(endpointMsg, "POST", "?groupId="+groupId));
        return callServiceMethod(() -> lotService.postLot(groupId, payload), HttpStatus.CREATED);
    }

    /**
     * The putLot method handles the /api/v1/lot?groupId=someVal&lotId=someVal endpoint.
     * The method throws a 404 if the groupId or lotId does not exist.
     * The method throws a 403 if the lot does not belong to the group.
     * 
     * @see PutLotPayload
     * 
     * @param groupId The Id of the group the lot belongs to.
     * @param lotId The Id of this lot.
     * @param payload The PutLotPayload for this request.
     * @return The updated lot.
     */
    @PutMapping(params = {"groupId","lotId"})
    public ResponseEntity<?> putLot(@RequestParam String groupId, @RequestParam Integer lotId, @Valid @RequestBody PutLotPayload payload) {
        log.info(String.format(endpointMsg, "PUT", "?groupId="+groupId+"&lotId="+lotId));
        return callServiceMethod(() -> lotService.putLot(groupId, lotId, payload), HttpStatus.OK);
    }

    /**
     * The deleteLot method handles the /api/v1/lot?groupId=someVal&lotId=someVal endpoint.
     * The method throws a 404 if the groupId or lotId does not exist.
     * 
     * @param groupId The Id of the group the lot belongs to.
     * @param lotId The Id of this lot.
     * @return no content, confirming the lot was removed.
     */
    @DeleteMapping(params = {"groupId", "lotId"})
    public ResponseEntity<?> deleteLot(@RequestParam String groupId, @RequestParam Integer lotId) {
        log.info(String.format(endpointMsg, "DELETE", "?groupId="+groupId+"&lotId="+lotId));
        return callVoidServiceMethod(() -> lotService.deleteLot(groupId, lotId), HttpStatus.NO_CONTENT);
    }

    /**
     * The deleteAllLots method handles the /api/v1/lot?groupId=someVal endpoint.
     * The method throws a 404 if the groupId does not exist.
     * 
     * @param groupId The Id of the group the lot belongs to.
     * @return no content, confirming all the lots were removed.
     */
    @DeleteMapping(params = {"groupId"})
    public ResponseEntity<?> deleteAllLots(@RequestParam String groupId) {
        log.info(String.format(endpointMsg, "DELETE", "?groupId="+groupId));
        return callVoidServiceMethod(() -> lotService.deleteAllLots(groupId), HttpStatus.NO_CONTENT);

    }

}
