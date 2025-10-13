package com.lotlytics.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lotlytics.api.entites.user.User;

/*
 * The UserRepository defines a JpaRepository to interact with the underlying
 * relational database with the user DTO.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
