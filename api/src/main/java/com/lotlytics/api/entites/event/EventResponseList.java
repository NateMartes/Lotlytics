package com.lotlytics.api.entites.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.ArrayList;

@AllArgsConstructor
@Getter
public class EventResponseList {
    private ArrayList<EventResponse> events;
}