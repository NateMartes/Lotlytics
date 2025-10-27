package com.lotlytics.api.entites.lot;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

/*
 * Represents the payload used to update an existing Lot.
 */
@Getter
@Setter
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
    
    /** The street of the lot */
    @Size(max = 255, message = "Lot street cannot be greater than 255 characters")
    private String street;

    /** The city the lot resides in */
    @Size(max = 255, message = "Lot cities cannot be greater than 255 characters")
    private String city;

    /** The state the lot resides in */
    @Size(max = 255, message = "Lot states cannot be greater than 255 characters")
    private String state;

    /** The zip code of the lot */
    @Size(max = 20, message = "Lot zip codes cannot be greater than 20 characters")
    private String zip;
}
