package com.lotlytics.api.entites.lot;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LotResponse {
    private Integer id;
    private String name;
    private Integer groupId;
    private Integer currentVolume;
    private Integer capacity;
    private String createdAt;
    private String updatedAt;
}
