package com.lotlytics.api.entites.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/*
 * Represents a Group entity stored in a relational database.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "groups")
public class Group {

    /* The unique identifier for the group. Primary key in the database. */
    @Id
    private String id;

    /* The name of the group. */
    private String name;
}

