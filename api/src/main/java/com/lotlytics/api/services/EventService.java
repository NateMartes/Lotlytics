package com.lotlytics.api.services;

import org.springframework.stereotype.Service;
import com.lotlytics.api.repositories.EventRepository;
import lombok.extern.slf4j.Slf4j;
import com.lotlytics.api.entites.event.CreateEventPayload;
import com.lotlytics.api.entites.event.Event;
import com.lotlytics.api.controllers.EventController;
import com.lotlytics.api.entites.exceptions.NotFoundException;
import java.util.List;

/**
 * The EventService class defines service methods that are used by the
 * EventController.
 * 
 * @see EventController
 */
@Slf4j
@Service
public class EventService {
    
    EventRepository eventRepository;
    LotService lotService;
    GroupService groupService;

    /**
     * The EventService class defines service methods that are used by the
     * EventController.
     * 
     * @see EventRepository
     */
    public EventService(EventRepository eventRepository, LotService lotService, GroupService groupService) {
        this.eventRepository = eventRepository;
        this.lotService = lotService;
        this.groupService = groupService;
    }

    /**
     * The getLotEvents method gets all the events that have the same groupId
     * and lotId.
     * 
     * @param groupId The Id of a group.
     * @param lotId The Id of the lot.
     * @throws NotFoundException
     * @return A list of events.
     */
    public List<Event> getLotEvents(String groupId, Integer lotId) throws NotFoundException {
        if (!groupService.isAGroup(groupId)) {
            throw new NotFoundException("Group Id does not exist");
        }
        if (!lotService.isALot(lotId)) {
            throw new NotFoundException("Lot Id does not exist");
        }
        List<Event> out = eventRepository.getEventsByLot(groupId, lotId);
        log.info("Gathered Events for lot " + lotId + " for " + groupId);
        return out;
    }

    /**
     * The getGroupEvents method gets all the events that have the same groupId.
     * 
     * @param groupId The Id of the group.
     * @throws NotFoundException
     * @return A list of events.
     */
    public List<Event> getGroupEvents(String groupId) throws NotFoundException {
        if (!groupService.isAGroup(groupId)) {
            throw new NotFoundException("Group Id does not exist");
        }
        List<Event> out = eventRepository.getEventsByGroup(groupId);
        log.info("Gathered Events for "+groupId);
        return out;
    }

    /**
     * The saveEvent method saves an Event into storage.
     * 
     * @param groupId The Id of the group.
     * @param lotId The Id of the lot.
     * @param event The payload representing an event.
     * @return The new created event.
     */
    public Event saveEvent(String groupId, Integer lotId, CreateEventPayload event) throws NotFoundException {
        if (!groupService.isAGroup(groupId)) {
            throw new NotFoundException("Group Id does not exist");
        }
        if (!lotService.isALot(lotId)) {
            throw new NotFoundException("Lot Id does not exist");
        }
        Event out = eventRepository.saveEvent(groupId, lotId, event);
        log.info("Saved Event for lot " + lotId + " for " + groupId + " with value " + event.getValue());
        return out;
    }
}