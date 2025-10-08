package com.lotlytics.api.repositories;

import com.lotlytics.api.entites.event.CreateEventPayload;
import com.lotlytics.api.entites.event.Event;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class EventRepository {


    private DynamoDbTable<Event> table;
    private static final Logger log = LoggerFactory.getLogger(EventRepository.class);

    // Adding Logging would be a good idea.
    public EventRepository(DynamoDbTable<Event> table) {
        this.table = table;
    }

    public List<Event> getAllEvents() {
        return table.scan().items().stream().collect(Collectors.toList());
    }

    public List<Event> getEventsByLot(Integer lotId) {
        SdkIterable<Page<Event>> pages = table.index("LotCapturedAtIndex")
            .query(r -> r.queryConditional(
                QueryConditional.keyEqualTo(k -> k.partitionValue(lotId))
            ));

        return StreamSupport.stream(pages.spliterator(), false)
        .flatMap(page -> page.items().stream())
        .collect(Collectors.toList());   
    }

    public List<Event> getEventsByGroup(String groupId) {
        SdkIterable<Page<Event>> pages = table.index("GroupCapturedAtIndex")
            .query(r -> r.queryConditional(
                QueryConditional.keyEqualTo(k -> k.partitionValue(groupId))
            ));

        return StreamSupport.stream(pages.spliterator(), false)
        .flatMap(page -> page.items().stream())
        .collect(Collectors.toList());   
    }

    public Event saveEvent(String groupId, Integer lotId, CreateEventPayload event) {
        log.info("Saving event for groupId: {}, lotId: {}", groupId, lotId);
        try {
            Event newEvent = new Event();
            newEvent.setId(UUID.randomUUID().toString());
            newEvent.setGroupId(groupId);
            newEvent.setLotId(lotId);
            newEvent.setCapturedAt(event.getCapturedAt());
            newEvent.setValue(event.getValue());
            
            log.debug("Event object created: {}", newEvent);
            table.putItem(newEvent);
            log.info("Event saved successfully with id: {}", newEvent.getId());
            
            return newEvent;
        } catch (Exception e) {
            log.error("Error saving event", e);
            throw e;
        }
    }
}

