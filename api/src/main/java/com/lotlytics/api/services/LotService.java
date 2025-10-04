package com.lotlytics.api.services;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import com.lotlytics.api.entites.lot.Lot;
import com.lotlytics.api.repositories.LotRepository;
import java.util.List;

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
}
