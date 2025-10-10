package com.lotlytics.api.entites.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import jakarta.validation.constraints.AssertTrue;

/*
 * Represents the payload used to create an event.
 */
@AllArgsConstructor
@Getter
public class CreateEventPayload {

    /** The numeric event value. Must be either 1 or -1. */
    private Integer value;

    /** The timestamp representing when the event was captured. */
    private String capturedAt;

    /**
     * The isValidValue method validates that the value field is either 1 or -1.
     *
     * This method is automatically invoked during Jakarta Bean Validation
     * when an instance of CreateEventPayload is validated.
     *
     * @return true if value is not null and equals 1 or -1, false otherwise.
     */
    @AssertTrue(message = "Value must be either 1 or -1")
    private boolean isValidValue() {
        return value != null && (value == 1 || value == -1);
    }
}

