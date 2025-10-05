package com.lotlytics.api.entites.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import jakarta.validation.constraints.Size;

@AllArgsConstructor
@Getter
public class CreateGroupPayload {
    @Size(max = 246, message = "Group names cannot be greater than 246 characters")
    String name;
}
