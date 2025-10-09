package com.lotlytics.api.entites.lot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Integer id;
    private String name;
    private String groupId;
    private Integer currentVolume;
    private Integer capacity;

    // Postgres Manages time, so don't allow inserts or updates
    @Column(insertable = false, updatable = false)
    private String createdAt;
    @Column(insertable = false, updatable = false)
    private String updatedAt;

    // For POST requests
    public Lot(String groupId, String name, Integer currentVolume, Integer capacity) {
        this.groupId = groupId;
        this.name = name;
        this.currentVolume = currentVolume;
        this.capacity = capacity;
    }
}
