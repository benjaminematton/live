package com.example.live_backend.repository;

import com.example.live_backend.model.User.Group;
import com.example.live_backend.model.User.GroupMembership;
import com.example.live_backend.model.User.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> { 
    List<GroupMembership> findByGroup(Group group);
    List<GroupMembership> findByUser(User user);
}
