package com.lotlytics.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lotlytics.api.entites.role.Role;

/*
 * The RoleRepository defines a JpaRepository to interact with the underlying
 * relational database with the Role DTO.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
