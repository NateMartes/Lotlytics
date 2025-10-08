package com.lotlytics.api.repositories;

import com.lotlytics.api.config.AwsDynamoProperties;
import com.lotlytics.api.entites.event.Event;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class EventRepository {

    private DynamoDbTable<Event> eventTable;

    // Adding Logging would be a good idea.
    public EventRepository(DynamoDbEnhancedClient enhancedClient, AwsDynamoProperties properties) {
        this.eventTable = enhancedClient.table(properties.getDefaultTableName(), TableSchema.fromBean(Event.class));
    }

    public List<Event> getAllEvents() {
        return eventTable.scan().items().stream().collect(Collectors.toList());
    }

    public void saveEvent(Event event) {
        eventTable.putItem(event);
    }

    public Optional<Event> getEventById(Integer id) {
        Event key = new Event();
        key.setId(id);
        return Optional.ofNullable(eventTable.getItem(key));
    }
}

