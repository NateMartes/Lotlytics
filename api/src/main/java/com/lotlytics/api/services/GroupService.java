package com.lotlytics.api.services;

import java.util.UUID;
import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import com.lotlytics.api.entites.group.CreateGroupPayload;
import com.lotlytics.api.entites.group.Group;
import com.lotlytics.api.repositories.GroupRepository;

@Service
public class GroupService {
    
    Integer RANDOM_ID_LENGTH = 8;
    GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public boolean isAGroup(String groupId) {
        return groupRepository.existsById(groupId);
    }

    public Group createGroup(CreateGroupPayload payload) {

        String randomId = UUID.randomUUID().toString();
        String name = payload.getName();
        String id = name + '-' + randomId.substring(0, RANDOM_ID_LENGTH);

        Group g = new Group(id, name);
        return groupRepository.save(g);
    }

    public List<Group> getGroup(String name) {
        Group g = new Group();
        g.setName(name);
        return groupRepository.findAll(Example.of(g));
    }

    public void deleteGroup(String groupId) {
        
        groupRepository.deleteById(groupId);
    }
}
