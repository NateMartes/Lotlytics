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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.ZonedDateTime;

/*
 * Represents a Lot entity stored in the relational database.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "lots")
public class Lot {

    /** The unique identifier for the lot. Primary key in the database, auto-generated. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Integer id;

    /** The name of the lot. */
    private String name;

    /** The ID of the group this lot belongs to. */
    private String groupId;

    /** The current volume used in the lot. */
    private Integer currentVolume;

    /** The maximum capacity of the lot. */
    private Integer capacity;

    /** The timestamp when the lot was created. Set automatically and not updatable. */
    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime createdAt;

    /** The timestamp of the last update to the lot. Set automatically. */
    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    /**
     * Constructs a Lot with the specified group ID, name, current volume, and capacity.
     *
     * @param groupId the ID of the group this lot belongs to
     * @param name the name of the lot
     * @param currentVolume the current volume of the lot
     * @param capacity the maximum capacity of the lot
     */
    public Lot(String groupId, String name, Integer currentVolume, Integer capacity) {
        this.groupId = groupId;
        this.name = name;
        this.currentVolume = currentVolume;
        this.capacity = capacity;
    }
}
