package com.lotlytics.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import com.lotlytics.api.entites.role.CreateRolePayload;
import com.lotlytics.api.services.RoleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * The RoleController class handles requests and responses for
 * the /api/v1/role endpoint.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/role")
public class RoleController extends GenericController {
    
    RoleService roleService;
    private static String endpointMsg = "%s /api/v1/role%s";
    
    /**
     * The RoleController class handles requests and responses for
     * the /api/v1/role endpoint.
     * 
     * @see RoleService
     * 
     * @param roleService A RoleService bean providing service methods
     */
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    
    /**
     * The getRole method handles the /api/v1/role?name=someVal endpoint.
     * 
     * @param name The name of the role
     * @return A list of roles with the matching name
     */
    @GetMapping(params = "name")
    public ResponseEntity<?> getRole(@RequestParam String name) {
        log.info(String.format(endpointMsg, "GET", "?name=" + name));
        return callServiceMethod(() -> roleService.getRole(name), HttpStatus.OK);
    }
    
    /**
     * The getRoles method handles the /api/v1/role endpoint.
     * 
     * @return A list of all known roles
     */
    @GetMapping
    public ResponseEntity<?> getRoles() {
        log.info(String.format(endpointMsg, "GET", ""));
        return callServiceMethod(() -> roleService.getRoles(), HttpStatus.OK);
    }
    
    /**
     * The createRole method handles the /api/v1/role endpoint.
     * This method creates a new role using the RoleService.
     * 
     * @see CreateRolePayload
     * 
     * @param payload The CreateRolePayload for this request
     * @return The new role
     */
    @PostMapping
    public ResponseEntity<?> createRole(@Valid @RequestBody CreateRolePayload payload) {
        log.info(String.format(endpointMsg, "POST", ""));
        return callServiceMethod(() -> roleService.createRole(payload), HttpStatus.CREATED);
    }
    
    /**
     * The deleteRole method handles the /api/v1/role?id=someVal endpoint.
     * This method removes a role using the RoleService.
     * 
     * @param id The ID of the role to delete
     * @return no content, confirming the role was removed
     */
    @DeleteMapping(params = "id")
    public ResponseEntity<?> deleteRole(@RequestParam Integer id) {
        log.info(String.format(endpointMsg, "DELETE", "?id=" + id));
        return callVoidServiceMethod(() -> roleService.deleteRole(id), HttpStatus.NO_CONTENT);
    }
}