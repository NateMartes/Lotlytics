package com.lotlytics.api.services;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.lotlytics.api.entites.lot.CreateLotPayload;
import com.lotlytics.api.entites.lot.Lot;
import com.lotlytics.api.entites.lot.PutLotPayload;
import com.lotlytics.api.repositories.LotRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public Lot getLot(String groupId, Integer lotId) throws EntityNotFoundException {
        Lot l = new Lot();
        l.setGroupId(groupId);
        l.setId(lotId);
        Optional<Lot> out = lotRepository.findOne(Example.of(l));
        if (out.isEmpty()) {
            throw new EntityNotFoundException();
        }
        return out.get();
    }

    public Lot postLot(String groupId, CreateLotPayload payload) {
        Integer capacity = payload.getCapacity();
        Integer currentVolume = payload.getVolume();
        String name = payload.getName();

        Lot newLot = new Lot(name, groupId, currentVolume, capacity);
        return lotRepository.save(newLot);
    }

    public Lot putLot(String groupId, Integer lotId, PutLotPayload updatedVariables) {
        Integer capacity = updatedVariables.getCapacity();
        Integer currentVolume = updatedVariables.getVolume();
        String name = updatedVariables.getName();

        Lot lot = getLot(groupId, lotId);
        
        if (capacity != null) {
            lot.setCapacity(capacity);
        }
        if (currentVolume != null) {
            lot.setCurrentVolume(currentVolume);
        }
        if (name != null) {
             lot.setName(name);
        }

        return lotRepository.save(lot);
    }

    public void deleteLot(String groupId, Integer lotId) {
        lotRepository.deleteById(lotId);
    }

    public void deleteAllLosts(String groupId) {
        Lot l = new Lot();
        l.setGroupId(groupId);
        List<Lot> entites = lotRepository.findAll(Example.of(l));
        lotRepository.deleteAll(entites);
    }
}
