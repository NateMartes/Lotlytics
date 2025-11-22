package com.lotlytics.api.entites.user;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    /** The primary key of a user */
    private Integer id;

    /** The username of a user */
    private String username;

    /** The email of a user */
    private String email;

    /** The password of a user */
    private String password;

    @Column(insertable = false, updatable = false)
    @CreationTimestamp
    /** When the user was created at */
    private ZonedDateTime created_at;

    public User(String username, String email, String password) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    // Roles will be managed by the database
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }
}
