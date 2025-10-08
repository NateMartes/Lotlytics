package com.lotlytics.api.services;

import org.springframework.stereotype.Service;
import com.lotlytics.api.repositories.EventRepository;
import com.lotlytics.api.entites.event.Event;
import java.util.List;

@Service
public class EventService {
    
    EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getLotEvents(String groupId, Integer lotId) {
        return eventRepository.getAllEvents();
    }
}
