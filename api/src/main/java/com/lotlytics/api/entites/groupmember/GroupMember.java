package com.lotlytics.api.entites.groupmember;

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

/**
 * Represents a GroupMember entity stored in the relational database.
 * Links users to groups with specific roles.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "group_members")
public class GroupMember {
    /** The unique identifier for the group member. Primary key in the database, auto-generated. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable = false, updatable = false)
    private Integer id;
    
    /** The ID of the group this member belongs to. */
    private String groupId;
    
    /** The ID of the user who is a member of the group. */
    private Integer userId;
    
    /** The ID of the role assigned to this user in the group. */
    private Integer roleId;
    
    /**
     * Constructs a GroupMember with the specified group ID, user ID, and role ID.
     *
     * @param groupId the ID of the group
     * @param userId the ID of the user
     * @param roleId the ID of the role assigned to the user
     */
    public GroupMember(String groupId, Integer userId, Integer roleId) {
        this.groupId = groupId;
        this.userId = userId;
        this.roleId = roleId;
    }
}