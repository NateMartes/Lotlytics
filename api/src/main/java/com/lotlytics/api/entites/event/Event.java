package com.lotlytics.api.entites.event;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

/**
 * Represents an Event stored in DynamoDB.
 */
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Event {

    /** The unique identifier for the event. Primary partition key in DynamoDB. */
    private String id;

    /** The lot ID associated with this event. Used as a secondary partition key in "LotCapturedAtIndex". */
    private Integer lotId;

    /** The group ID associated with this event. Used as a secondary partition key in "GroupCapturedAtIndex". */
    private String groupId;

    /** The numeric value of the event (e.g., 1 or -1). */
    private Integer value;

    /** The timestamp when the event was captured. Used as secondary sort key in indexes. */
    private String capturedAt;

    /**
     * The getId method returns the id for this event.
     *
     * @return the event ID
     */
    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    /**
     * The getLotId method returns the lot ID for this event.
     *
     * @return the lot ID
     */
    @DynamoDbSecondaryPartitionKey(indexNames = "LotCapturedAtIndex")
    public Integer getLotId() {
        return lotId;
    }

    /**
     * Returns the group ID for this event.
     *
     * @return the group ID
     */
    @DynamoDbSecondaryPartitionKey(indexNames = "GroupCapturedAtIndex")
    public String getGroupId() {
        return groupId;
    }

    /**
     * Returns the captured timestamp used as the sort key for
     * the secondary indexes "LotCapturedAtIndex" and "GroupCapturedAtIndex".
     *
     * @return the captured timestamp
     */
    @DynamoDbSecondarySortKey(indexNames = {"LotCapturedAtIndex", "GroupCapturedAtIndex"})
    public String getCapturedAt() {
        return capturedAt;
    }
}
