package com.lotlytics.api.services;

import java.util.List;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import com.lotlytics.api.entites.exceptions.ConflictException;
import com.lotlytics.api.entites.exceptions.NotFoundException;
import com.lotlytics.api.entites.role.CreateRolePayload;
import com.lotlytics.api.entites.role.Role;
import com.lotlytics.api.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * The RoleService class defines service methods for managing roles.
 */
@Slf4j
@Service
public class RoleService {
    
    RoleRepository roleRepository;
    
    /**
     * Constructs a RoleService with the specified RoleRepository.
     * 
     * @param roleRepository the repository for role operations
     */
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    /**
     * The createRole method creates a new role.
     * 
     * @param payload a CreateRolePayload that defines a new role
     * @return The newly created role
     * @throws ConflictException if a role with the same name already exists
     */
    public Role createRole(CreateRolePayload payload) throws ConflictException {
        String name = payload.getName();
        
        // Check if role with this name already exists
        Role existingRole = new Role();
        existingRole.setName(name);
        List<Role> existingRoles = roleRepository.findAll(Example.of(existingRole));
        
        if (!existingRoles.isEmpty()) {
            throw new ConflictException("Role with name '" + name + "' already exists");
        }
        
        Role role = new Role(name);
        Role out = roleRepository.save(role);
        log.info("Created role '" + name + "'");
        return out;
    }
    
    /**
     * The getRoles method gets all roles in the database.
     * 
     * @return A list of all roles
     */
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }
    
    /**
     * The getRole method gets a role based on name.
     * 
     * @param name a possible role name
     * @return A list of roles found with the given name
     */
    public List<Role> getRole(String name) {
        Role role = new Role();
        role.setName(name);
        return roleRepository.findAll(Example.of(role));
    }
    
    /**
     * The deleteRole method removes a role from the database.
     * 
     * @param id the ID of the role to delete
     * @throws NotFoundException if the role does not exist
     */
    public void deleteRole(Integer id) {
        // Check if role exists
        if (!roleRepository.existsById(id)) {
            throw new NotFoundException("Role with ID '" + id + "' does not exist");
        }
        
        // Delete the role
        roleRepository.deleteById(id);
        log.info("Deleted role with ID '" + id + "'");
    }
}