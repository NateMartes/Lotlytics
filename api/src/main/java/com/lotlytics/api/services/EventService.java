package com.lotlytics.api.services;

import org.springframework.stereotype.Service;
import com.lotlytics.api.repositories.EventRepository;
import com.lotlytics.api.entites.event.CreateEventPayload;
import com.lotlytics.api.entites.event.Event;
import java.util.List;

@Service
public class EventService {
    
    EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getLotEvents(String groupId, Integer lotId) {
        return eventRepository.getEventsByLot(groupId, lotId);
    }

    public List<Event> getGroupEvents(String groupId) {
        return eventRepository.getEventsByGroup(groupId);
    }

    public Event saveEvent(String groupId, Integer lotId, CreateEventPayload event) {
        return eventRepository.saveEvent(groupId, lotId, event);
    }
}
