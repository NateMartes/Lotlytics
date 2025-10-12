package com.lotlytics.api.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a simple error message DTO used for API responses.
 */
@AllArgsConstructor
@Getter
public class ErrorMessage {

    /** The content of the error message. */
    private String error;
}
