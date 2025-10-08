package com.lotlytics.api.entites.event;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Event {
    private String id;
    private Integer lotId;
    private String groupId;
    private Integer value;
    private String capturedAt;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
}
