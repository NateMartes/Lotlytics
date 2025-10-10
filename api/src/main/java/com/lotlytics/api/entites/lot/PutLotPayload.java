package com.lotlytics.api.entites.lot;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/*
 * Represents the payload used to update an existing Lot.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor 
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PutLotPayload {

    /** The name of the lot. Maximum 255 characters. */
    @Size(max = 255, message = "Lot names cannot be greater than 255 characters")
    private String name;

    /** The maximum capacity of the lot. */
    private Integer capacity;

    /** The current volume of the lot. */
    private Integer volume;
}
