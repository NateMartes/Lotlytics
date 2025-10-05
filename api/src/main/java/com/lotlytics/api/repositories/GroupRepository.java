package com.lotlytics.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lotlytics.api.entites.group.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {
}
