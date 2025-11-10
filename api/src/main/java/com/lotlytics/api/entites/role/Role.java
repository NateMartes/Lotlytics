package com.lotlytics.api.entites.role;

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
import java.time.ZonedDateTime;

/**
 * Represents a Role entity stored in the relational database.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    /** The unique identifier for the role. Primary key in the database, auto-generated. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Integer id;
    
    /** The name of the role. */
    private String name;
    
    /** The timestamp when the role was created. Set automatically and not updatable. */
    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime createdAt;
    
    /**
     * Constructs a Role with the specified name.
     *
     * @param name the name of the role
     */
    public Role(String name) {
        this.name = name;
    }
}