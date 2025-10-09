package com.lotlytics.api.entites.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import jakarta.validation.constraints.AssertTrue;

@AllArgsConstructor
@Getter
public class CreateEventPayload {
    private Integer value;
    private String capturedAt;
    
    @AssertTrue(message = "Value must be either 1 or -1")
    private boolean isValidValue() {
        return value != null && (value == 1 || value == -1);
    }
}
