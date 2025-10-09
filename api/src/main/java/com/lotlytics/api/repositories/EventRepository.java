package com.lotlytics.api.repositories;

import com.lotlytics.api.entites.event.CreateEventPayload;
import com.lotlytics.api.entites.event.Event;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.UUID;

@Slf4j
@Repository
public class EventRepository {

    private DynamoDbTable<Event> table;

    public EventRepository(DynamoDbTable<Event> table) {
        this.table = table;
    }

    public List<Event> getAllEvents() {
        return table.scan().items().stream().collect(Collectors.toList());
    }

    private List<Event> getEventsByLotStandardIndex(String groupId, Integer lotId) {
        return table.scan().items().stream()
            .filter(event -> groupId.equals(event.getGroupId()) && lotId.equals(event.getLotId()))
            .collect(Collectors.toList());
    }

    public List<Event> getEventsByGroupStandardIndex(String groupId) {
        return table.scan().items().stream()
            .filter(event -> groupId.equals(event.getGroupId()))
            .collect(Collectors.toList());
    }


    public List<Event> getEventsByLot(String groupId, Integer lotId) {
        try {
            SdkIterable<Page<Event>> pages = table.index("LotCapturedAtIndex")
                .query(r -> r.queryConditional(
                    QueryConditional.keyEqualTo(k -> k.partitionValue(lotId))
                ));
            List<Event> output = StreamSupport.stream(pages.spliterator(), false)
                .flatMap(page -> page.items().stream())
                .filter(event -> groupId.equals(event.getGroupId()))
                .collect(Collectors.toList());

            log.info("Queried "+output.size()+" Events for "+groupId+" at lot "+lotId);
            return output;

        } catch (ResourceNotFoundException e) {
            log.warn(e.toString());
            log.info("Defaulting to no Global Secondary Index (GSI)");
            return getEventsByLotStandardIndex(groupId, lotId);
        } catch (Exception e) {
            log.error(e.toString());
            throw e;
        }
    }

    public List<Event> getEventsByGroup(String groupId) {
        try {
            SdkIterable<Page<Event>> pages = table.index("GroupCapturedAtIndex")
                .query(r -> r.queryConditional(
                    QueryConditional.keyEqualTo(k -> k.partitionValue(groupId))
                ));

            List<Event> output = StreamSupport.stream(pages.spliterator(), false)
                .flatMap(page -> page.items().stream())
                .filter(event -> groupId.equals(event.getGroupId()))
                .collect(Collectors.toList());

            log.info("Queried "+output.size()+" Events for "+groupId);
            return output;

        } catch (ResourceNotFoundException e) {
            log.warn(e.toString());
            log.info("Defaulting to no Global Secondary Index (GSI)");
            return getEventsByGroupStandardIndex(groupId);
        } 
    }

    public Event saveEvent(String groupId, Integer lotId, CreateEventPayload event) {
        try {
            Event newEvent = new Event();
            newEvent.setId(UUID.randomUUID().toString());
            newEvent.setGroupId(groupId);
            newEvent.setLotId(lotId);
            newEvent.setCapturedAt(event.getCapturedAt());
            newEvent.setValue(event.getValue());
            table.putItem(newEvent);
            log.info("Stored Event for "+groupId+" at "+lotId+" with value "+newEvent.getValue());
            return newEvent;
        } catch (Exception e) {
            log.error(e.toString());
            throw e;
        }
    }
}

