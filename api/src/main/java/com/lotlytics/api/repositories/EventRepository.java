package com.lotlytics.api.repositories;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Repository;
import com.lotlytics.api.entites.event.CreateEventPayload;
import com.lotlytics.api.entites.event.Event;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

/*
 * The EventRepository provides methods for interacting with a DynamoDB table.
 */
@Slf4j
@Repository
public class EventRepository {

    private static String GLOBAL_SECONDARY_INDEX_LOT = "LotCapturedAtIndex";
    private static String GLOBAL_SECONDARY_INDEX_GROUP = "GroupCapturedAtIndex";
    private DynamoDbTable<Event> table;

    /**
     * The EventRepository provides methods for interacting with a DynamoDB table.
     * 
     * @param table a DynamoDbTable<Event> bean
     */
    public EventRepository(DynamoDbTable<Event> table) {
        this.table = table;
    }

    /**
     * The getAllEvents method returns all events stored in the DynamoDB database.
     * This method is quite inefficent, so it is adivsed to not use it and instead use the
     * standard index method
     * 
     * @see getEventsByLotStandardIndex
     * 
     * @return a list of events in the DynamoDB database.
     */
    public List<Event> getAllEvents() {
        return table.scan().items().stream().collect(Collectors.toList());
    }

    /**
     * The getEventsByLotStandardIndex method returns all events stored in the DynamoDB database
     * based on the groupId and lotId of the event.
     * This method is quite inefficent, so it is adivsed to not use it and instead use the
     * global secondary index method
     * 
     * @see getEventsByLot
     * 
     * @return a list of events in the DynamoDB database.
     */
    private List<Event> getEventsByLotStandardIndex(String groupId, Integer lotId) {
        return table.scan().items().stream()
            .filter(event -> groupId.equals(event.getGroupId()) && lotId.equals(event.getLotId()))
            .collect(Collectors.toList());
    }

    /**
     * The getEventsByGroupStandardIndex method returns all events stored in the DynamoDB database
     * based on the groupId.
     * This method is quite inefficent, so it is adivsed to not use it and instead use the
     * global secondary index method
     * 
     * @see getEventsByGroup
     * 
     * @return a list of events in the DynamoDB database.
     */
    public List<Event> getEventsByGroupStandardIndex(String groupId) {
        return table.scan().items().stream()
            .filter(event -> groupId.equals(event.getGroupId()))
            .collect(Collectors.toList());
    }

    /**
     * The getEventsByLot method returns all events stored in the DynamoDB database
     * based on the groupId and lotId.
     * If the GLOBAL_SECONDARY_INDEX_LOT is not defined in the database, this method falls back
     * on getEventsByLotStandardIndex.
     * 
     * @see getEventsByLotStandardIndex
     * 
     * @return a list of events in the DynamoDB database.
     */
    public List<Event> getEventsByLot(String groupId, Integer lotId) {
        try {
            SdkIterable<Page<Event>> pages = table.index(GLOBAL_SECONDARY_INDEX_LOT)
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

    /**
     * The getEventsByGroup method returns all events stored in the DynamoDB database
     * based on the groupId.
     * If the GLOBAL_SECONDARY_INDEX_GROUP is not defined in the database, this method falls back
     * on getEventsByGroupStandardIndex.
     * 
     * @see getEventsByGroupStandardIndex
     * 
     * @return a list of events in the DynamoDB database.
     */
    public List<Event> getEventsByGroup(String groupId) {
        try {
            SdkIterable<Page<Event>> pages = table.index(GLOBAL_SECONDARY_INDEX_GROUP)
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

    /**
     * The saveEvent method storeds a new event in the DynamoDB database.
     * 
     * @return the new event in the DynamoDB database.
     */
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

