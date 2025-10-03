package com.lotlytics.api.entites.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateEventPayload {
    private String content;
}
