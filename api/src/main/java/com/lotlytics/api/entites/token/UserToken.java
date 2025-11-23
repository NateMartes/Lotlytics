package com.lotlytics.api.entites.token;

import java.time.ZonedDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * The User class represent a Lotlytics user
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_tokens")
public class UserToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    /** The primary key of a user token */
    private Integer id;

    /** The id of a user */
    private Integer userId;

    /* The token for the user's session */
    private String token;

    /* The time the token was created at */
    @CreationTimestamp
    @Column(updatable = false)
    private ZonedDateTime createdAt;

    /* The time the token expires at */
    private ZonedDateTime expiresAt;

}
