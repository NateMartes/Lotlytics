package com.lotlytics.api.services;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import com.lotlytics.api.entites.lot.CreateLotPayload;
import com.lotlytics.api.entites.lot.Lot;
import com.lotlytics.api.entites.lot.PutLotPayload;
import com.lotlytics.api.controllers.LotController;
import com.lotlytics.api.repositories.LotRepository;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import com.lotlytics.api.entites.exceptions.NotFoundException;
import com.lotlytics.api.entites.exceptions.BadRequestException;
import com.lotlytics.api.entites.geocoding.ValidatedLotAddressUSA;
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
    private GroupService groupService;
    private GeocodingService geocodingService;

    /**
     * The LotService class defines service methods that are used by the
     * LotController.
     * 
     * @see LotController
     */
    public LotService(LotRepository lotRepository, GroupService groupService, GeocodingService geocodingService) {
        this.lotRepository = lotRepository;
        this.geocodingService = geocodingService;
        this.groupService = groupService;
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
     * The getLotsByGroup method gets all lots based on groupId
     * 
     * @param groupId The Id of the group.
     * @throws BadRequestException
     * @return A list of lots.
     */
    public List<Lot> getLotsByGroup(String groupId) throws NotFoundException {
        System.out.println(groupId);
        if (!groupService.isAGroup(groupId)) {
            throw new NotFoundException("Group Id does not exist");
        }

        Lot l = new Lot();
        l.setGroupId(groupId);
        List<Lot> out = lotRepository.findAll(Example.of(l));
        
        log.info("Gathered lots for " + groupId);
        return out;
    }

    /**
     * The getLot method gets a possible lot given an id and group id.
     * 
     * @throws NotFoundException if the lot or group does not exist.
     * 
     * @param id The possible id of a lot.
     * @param groupId The possible id of a group.
     * @throws NotFoundException
     * @return A lot.
     */
    public Lot getLot(String groupId, Integer lotId) throws NotFoundException {

        Lot l = new Lot();
        l.setGroupId(groupId);
        if (!groupService.isAGroup(groupId)) {
            throw new NotFoundException("Group Id does not exist");
        }

        l.setId(lotId);
        Optional<Lot> out = lotRepository.findOne(Example.of(l));

        if (out.isEmpty()) {
            throw new NotFoundException("Lot does not exist");
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
    public Lot postLot(String groupId, CreateLotPayload payload) throws NotFoundException, BadRequestException {

        if (!groupService.isAGroup(groupId)) {
            throw new NotFoundException("Group Id does not exist");
        }

        Integer capacity = payload.getCapacity();
        Integer currentVolume = payload.getVolume();
        String name = payload.getName();
        String street = payload.getStreet();
        String state = payload.getState();
        String city = payload.getCity();
        String zip = payload.getZip();

        ValidatedLotAddressUSA validatedAddress = geocodingService.validateAddressInUS(street, city, state, zip);
        if (!validatedAddress.isValid()) {
            throw new BadRequestException("Address is not valid");
        }
        if (!validatedAddress.isInUSA()) {
            throw new BadRequestException("Non USA Address");
        }

        // Overwrite Values with the ones from the validated object
        street = validatedAddress.getStreet();
        city = validatedAddress.getCity();
        state = validatedAddress.getState();
        zip = validatedAddress.getZip();

        Lot newLot = new Lot(groupId, name, currentVolume, capacity, street, city, state, zip);
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
     * @throws NotFoundException
     * @return The newly updated lot.
     */
    public Lot putLot(String groupId, Integer lotId, PutLotPayload updatedVariables) throws NotFoundException, BadRequestException {
        if (!groupService.isAGroup(groupId)) {
            throw new NotFoundException("Group Id does not exist");
        }
        if (!isALot(lotId)) {
            throw new NotFoundException("Lot Id does not exist");
        }

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
        updateField.accept(updatedVariables.getStreet(), v -> lot.setStreet((String) v));
        updateField.accept(updatedVariables.getState(), v -> lot.setState((String) v));
        updateField.accept(updatedVariables.getCity(), v -> lot.setCity((String) v));
        updateField.accept(updatedVariables.getZip(), v -> lot.setZip((String) v));
        
        ValidatedLotAddressUSA validatedAddress = geocodingService.validateAddressInUS(lot.getStreet(), lot.getCity(), lot.getState(), lot.getZip());
        if (!validatedAddress.isValid()) {
            throw new BadRequestException("Address is not valid");
        }
        if (!validatedAddress.isInUSA()) {
            throw new BadRequestException("Non USA Address");
        }

        // Overwrite Values with the ones from the validated object
        lot.setStreet(validatedAddress.getStreet());
        lot.setCity(validatedAddress.getCity());
        lot.setState(validatedAddress.getState());
        lot.setZip(validatedAddress.getZip());

        Lot updatedLot = lotRepository.save(lot);
        log.info(logMsg.toString());
        return updatedLot;
    }

    /**
     * The deleteLot method removes a lot.
     * 
     * @param groupId The Id of the group the lot belongs to.
     * @param lotId The Id of the lot.
     * @throws NotFoundException
     */
    public void deleteLot(String groupId, Integer lotId) throws NotFoundException {
        if (!groupService.isAGroup(groupId)) {
            throw new NotFoundException("Group Id does not exist");
        }
        if (!isALot(lotId)) {
            throw new NotFoundException("Lot Id does not exist");
        }
        lotRepository.deleteById(lotId);
        log.info("Removed lot with ID "+lotId);
    }

    /**
     * The deleteAllLots method removes all lots for a group.
     * @param groupId The id of the group
     * @throws NotFoundException
     */
    public void deleteAllLots(String groupId) throws NotFoundException {
        if (!groupService.isAGroup(groupId)) {
            throw new NotFoundException("Group Id does not exist");
        }
        Lot l = new Lot();
        l.setGroupId(groupId);
        List<Lot> entites = lotRepository.findAll(Example.of(l));
        lotRepository.deleteAll(entites);
        log.info("Removed all lots for "+groupId);
    }
}