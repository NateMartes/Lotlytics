package com.lotlytics.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lotlytics.api.entites.group.Group;

/*
 * The GroupRepository defines a JpaRepository to interact with the underlying
 * relational database with the Group DTO.
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, String> {
}
