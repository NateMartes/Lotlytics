package com.lotlytics.api.services;

import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.lotlytics.api.entites.exceptions.NotFoundException;
import com.lotlytics.api.entites.group.CreateGroupPayload;
import com.lotlytics.api.entites.group.Group;
import com.lotlytics.api.repositories.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import com.lotlytics.api.controllers.GroupController;

/**
 * The GroupService class defines service methods that are used by the
 * GroupController.
 * 
 * @see GroupController
 */
@Slf4j
@Service
public class GroupService {
    
    Integer RANDOM_ID_LENGTH = 8;
    GroupRepository groupRepository;

    /**
     * The GroupService class defines service methods that are used by the
     * GroupController.
     * 
     * @see GroupController
     */
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    /**
     * The isAGroup method determines if a group exists.
     * 
     * @param groupId a possible group.
     * @return true if the group exists within the database. Returns false otherwise.
     */
    public boolean isAGroup(String groupId) {
        return groupRepository.existsById(groupId);
    }

    /**
     * The createGroup method creates a new unique group.
     * 
     * @param payload a CreateGroupPayload that defines a new group.
     * @return The new created group.
     */
    public Group createGroup(CreateGroupPayload payload) {

        String randomId = UUID.randomUUID().toString();
        String name = payload.getName();
        String id = name + '-' + randomId.substring(0, RANDOM_ID_LENGTH);

        Group g = new Group(id, name);
        Group out = groupRepository.save(g);
        log.info("Created group '" + id +"'");
        return out;
    }

    /**
     * The getGroup method gets a group based on name.
     * 
     * @param name a possbile group name.
     * @return A list of groups found.
     */
    public List<Group> getGroup(String name) {
        if (name.equals("")) {
            return getAllGroups();
        }
        Group g = new Group();
        g.setName(name.toLowerCase());
        return groupRepository.findAll(Example.of(g));
    }

    /**
     * The getAllGroups method gets all known groups.
     * 
     * @return A list of groups found.
     */
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    /**
     * The deleteGroup removes a group based on ID.
     * 
     * @param groupId The Id of a group.
     */
    public void deleteGroup(String groupId) throws NotFoundException {
        if (!isAGroup(groupId)) {
            throw new NotFoundException("Group Id does not exist");
        }
        groupRepository.deleteById(groupId);
        log.info("Removed group '" + groupId +"'");
    }
}