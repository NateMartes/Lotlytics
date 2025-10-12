package com.lotlytics.api.services;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import com.lotlytics.api.entites.lot.CreateLotPayload;
import com.lotlytics.api.entites.lot.Lot;
import com.lotlytics.api.entites.lot.PutLotPayload;
import com.lotlytics.api.controllers.LotController;
import com.lotlytics.api.repositories.LotRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
/**
 * The LotService class defines service methods that are used by the
 * LotController.
 * 
 * @see LotController
 */
@Slf4j
@Service
public class LotService {

    private LotRepository lotRepository;

    /**
     * The LotService class defines service methods that are used by the
     * LotController.
     * 
     * @see LotController
     */
    public LotService(LotRepository lotRepository) {
        this.lotRepository = lotRepository;
    }

    /**
     * The getLotsByGroup method gets all lots based on groupId
     * 
     * @param groupId The Id of the group.
     * @return A list of lots
     */
    public List<Lot> getLotsByGroup(String groupId) {
        Lot l = new Lot();
        l.setGroupId(groupId);
        List<Lot> out = lotRepository.findAll(Example.of(l));
        log.info("Gathered lots for " + groupId);
        return out;
    }

    /**
     * The isALot method determines if a lot exists
     * 
     * @param id The possible id of a lot.
     * @return true if a lot exists. False otherwise.
     */
    public boolean isALot(Integer id) {
        return lotRepository.existsById(id);
    }

    /**
     * The getLot method gets a possible lot given an id and group id.
     * 
     * @throws EntityNotFoundException if the lot does not exist.
     * 
     * @param id The possible id of a lot.
     * @param groupId The possible id of a group.
     * @return A lot.
     */
    public Lot getLot(String groupId, Integer lotId) {
        Lot l = new Lot();
        l.setGroupId(groupId);
        l.setId(lotId);
        Optional<Lot> out = lotRepository.findOne(Example.of(l));
        if (out.isEmpty()) {
            log.warn("Lot with group " + groupId + " and ID "+lotId+" does not exist");
            throw new EntityNotFoundException();
        }
        return out.get();
    }

    /**
     * The postLot method creates a new lot.
     *  
     * @param payload The CreateLotPayload that defines a new lot.
     * @param groupId The id of a group.
     * @return The newly created lot.
     */
    public Lot postLot(String groupId, CreateLotPayload payload) {
        Integer capacity = payload.getCapacity();
        Integer currentVolume = payload.getVolume();
        String name = payload.getName();
        String address = payload.getAddress();
        String state = payload.getCity();
        String city = payload.getCity();
        String zip = payload.getZip();

        log.info("Address: "+address);
        Lot newLot = new Lot(groupId, name, currentVolume, capacity, address, city, state, zip);
        newLot = lotRepository.save(newLot);

        log.info("Created lot '" + name + "' for " + groupId + " with ID "+newLot.getId());
        return newLot;
    }

    /**
     * The putLot method updates a lot.
     *  
     * @param groupId The id of a group.
     * @param lotId The id of a lot.
     * @param updatedVariables The new variables to update for the lot.
     * @return The newly updated lot.
     */
    public Lot putLot(String groupId, Integer lotId, PutLotPayload updatedVariables) {
        
        Lot lot = getLot(groupId, lotId);
        StringBuilder logMsg = new StringBuilder("Updated lot " + lotId);

        // Helper lambda to update a field if not null
        BiConsumer<Object, Consumer<Object>> updateField = (value, setter) -> {
            if (value != null) {
                setter.accept(value);
                logMsg.append(" ").append(value.toString());
            }
        };

        updateField.accept(updatedVariables.getCapacity(), v -> lot.setCapacity((Integer) v));
        updateField.accept(updatedVariables.getVolume(), v -> lot.setCurrentVolume((Integer) v));
        updateField.accept(updatedVariables.getName(), v -> lot.setName((String) v));
        updateField.accept(updatedVariables.getAddress(), v -> lot.setAddress((String) v));
        updateField.accept(updatedVariables.getState(), v -> lot.setState((String) v));
        updateField.accept(updatedVariables.getCity(), v -> lot.setCity((String) v));
        updateField.accept(updatedVariables.getZip(), v -> lot.setZip((String) v));
        

        Lot updatedLot = lotRepository.save(lot);
        log.info(logMsg.toString());
        return updatedLot;
    }

    /**
     * The deleteLot method removes a lot.
     * 
     * @param groupId The Id of the group the lot belongs to.
     * @param lotId The Id of the lot.
     */
    public void deleteLot(String groupId, Integer lotId) {
        lotRepository.deleteById(lotId);
        log.info("Removed lot with ID "+lotId);
    }

    /**
     * The deleteAllLots method removes all lots for a group.
     * @param groupId The id of the group
     */
    public void deleteAllLots(String groupId) {
        Lot l = new Lot();
        l.setGroupId(groupId);
        List<Lot> entites = lotRepository.findAll(Example.of(l));
        lotRepository.deleteAll(entites);
        log.info("Removed all lots for "+groupId);
    }
}