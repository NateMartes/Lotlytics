package com.lotlytics.api.services;

import org.springframework.stereotype.Service;
import com.lotlytics.api.repositories.GroupRepository;

@Service
public class GroupService {
    
    GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public boolean isAGroup(String groupId) {
        return groupRepository.existsById(groupId);
    }
}
