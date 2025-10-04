package com.lotlytics.api.services;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import com.lotlytics.api.entites.lot.Lot;
import com.lotlytics.api.repositories.LotRepository;
import java.util.List;
import java.util.Optional;

@Service
public class LotService {

    private LotRepository lotRepository;

    public LotService(LotRepository lotRepository) {
        this.lotRepository = lotRepository;
    }

    public List<Lot> getLotsByGroup(String groupId) {
        Lot l = new Lot();
        l.setGroupId(groupId);
        List<Lot> out = lotRepository.findAll(Example.of(l));
        return out;
    }

    public boolean isALot(Integer id) {
        return lotRepository.existsById(id);
    }

    public Lot getLot(String groupId, Integer lotId) {
        Lot l = new Lot();
        l.setGroupId(groupId);
        l.setId(lotId);
        Optional<Lot> out = lotRepository.findOne(Example.of(l));
        if (out.isEmpty()) {
            throw new jakarta.persistence.EntityNotFoundException();
        }
        return out.get();
    } 
}
