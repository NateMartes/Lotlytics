package com.lotlytics.api.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lotlytics.api.entites.groupmember.GroupMember;

/*
 * The GroupMemberRepository defines a JpaRepository to interact with the underlying
 * relational database with the GroupMember DTO.
 */
@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {
}