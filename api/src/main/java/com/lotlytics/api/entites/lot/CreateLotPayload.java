package com.lotlytics.api.entites.lot;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateLotPayload {
    private String name;
    private Integer capacity;
    private Integer volume;
}