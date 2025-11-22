package com.lotlytics.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lotlytics.api.entites.token.UserToken;

/*
 * The UserTokensRepository defines a JpaRepository to interact with the underlying
 * relational database with the user tokens DTO.
 */
@Repository
public interface UserTokensRepository extends JpaRepository<UserToken, Integer> {
}

