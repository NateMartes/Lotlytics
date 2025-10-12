package com.lotlytics.api.entites.lot;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * Represents the payload used to create a new Lot.
 */
@AllArgsConstructor
@Getter
public class CreateLotPayload {

    /** The name of the lot. Maximum 255 characters. */
    @Size(max = 255, message = "Lot names cannot be greater than 255 characters")
    private String name;

    /** The address of the lot */
    @Size(max = 255, message = "Lot addresses cannot be greater than 255 characters")
    private String address;

    /** The city the lot resides in */
    @Size(max = 255, message = "Lot cities cannot be greater than 255 characters")
    private String city;

    /** The state the lot resides in */
    @Size(max = 255, message = "Lot states cannot be greater than 255 characters")
    private String state;

    /** The zip code of the lot */
    @Size(max = 20, message = "Lot zip codes cannot be greater than 20 characters")
    private String zip;

    /** The total capacity of the lot. Must be non-negative. */
    private Integer capacity;

    /** The current volume used in the lot. Must not exceed capacity. */
    private Integer volume;

    /**
     * The nonNegativeCapacity method validates that the capacity is non-negative.
     *
     * @return true if capacity is greater than or equal to 0, false otherwise.
     */
    @AssertTrue(message = "Capacity cannot be negative")
    private boolean nonNegativeCapacity() {
        return this.capacity >= 0;
    }

    /**
     * The isValidCapacity method validates that the capacity is greater than or equal to the volume.
     *
     * @return true if capacity is greater than or equal to volume, false otherwise.
     */
    @AssertTrue(message = "Capacity must be greater than or equal to volume")
    private boolean isValidCapacity() {
        return this.capacity >= this.volume;
    }
}
