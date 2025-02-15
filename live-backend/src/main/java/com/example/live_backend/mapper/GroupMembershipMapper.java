package com.example.live_backend.mapper;

import org.springframework.stereotype.Component;

import com.example.live_backend.model.User.Group;
import com.example.live_backend.model.User.GroupMembership;
import com.example.live_backend.model.User.User;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import com.example.live_backend.dto.Group.GroupMembershipRequest;
import com.example.live_backend.dto.Group.GroupMembershipResponse;

@Component
public class GroupMembershipMapper {
    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private UserMapper userMapper;

    public GroupMembershipResponse toResponse(GroupMembership groupMembership) {
        GroupMembershipResponse response = new GroupMembershipResponse();
        response.setId(groupMembership.getId());
        response.setGroup(groupMapper.toResponse(groupMembership.getGroup()));
        response.setUser(userMapper.toResponse(groupMembership.getUser()));
        response.setRole(groupMembership.getRole());
        response.setJoinedAt(groupMembership.getJoinedAt());
        return response;
    }

    public GroupMembership toEntity(GroupMembershipRequest request, Group group, User user) {
        GroupMembership groupMembership = new GroupMembership();
        groupMembership.setGroup(group);
        groupMembership.setUser(user);
        groupMembership.setRole(request.getRole());
        groupMembership.setJoinedAt(LocalDateTime.now());
        return groupMembership;
    }

    public GroupMembership updateEntity(GroupMembership groupMembership, GroupMembershipRequest request) {
        groupMembership.setRole(request.getRole());
        return groupMembership;
    }
}
