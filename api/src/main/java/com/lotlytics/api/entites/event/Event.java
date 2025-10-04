package com.lotlytics.api.entites.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Event {
    private Integer id;
    private Integer lotId;
    private String groupId;
    private Integer value;
    private String capturedAt;
}
