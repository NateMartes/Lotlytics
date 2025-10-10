package com.lotlytics.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lotlytics.api.entites.lot.Lot;

/*
 * The LotRepository defines a JpaRepository to interact with the underlying
 * relational database with the Lot DTO.
 */
@Repository
public interface LotRepository extends JpaRepository<Lot, Integer> {
}
