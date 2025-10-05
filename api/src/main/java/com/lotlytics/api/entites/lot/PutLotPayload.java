package com.lotlytics.api.entites.lot;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor 
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PutLotPayload {
    @Size(max = 255, message = "Lot names cannot be greater than 255 characters")
    private String name;
    private Integer capacity;
    private Integer volume;
}
