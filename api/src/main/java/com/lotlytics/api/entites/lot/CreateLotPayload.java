package com.lotlytics.api.entites.lot;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateLotPayload {
    @Size(max = 255, message = "Lot names cannot be greater than 255 characters")
    private String name;
    private Integer capacity;
    private Integer volume;
}