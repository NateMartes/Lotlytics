package com.lotlytics.api.entites.lot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "lots")
public class Lot {
    @Id
    private Integer id;
    private String name;
    private String groupId;
    private Integer currentVolume;
    private Integer capacity;
    private String createdAt;
    private String updatedAt;
}
