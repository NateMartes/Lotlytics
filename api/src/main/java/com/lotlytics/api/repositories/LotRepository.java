package com.lotlytics.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lotlytics.api.entites.lot.Lot;

@Repository
public interface LotRepository extends JpaRepository<Lot, Integer> {
}
