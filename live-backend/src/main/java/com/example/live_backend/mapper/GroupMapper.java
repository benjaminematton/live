package com.example.live_backend.mapper;

import org.springframework.stereotype.Component;

import com.example.live_backend.dto.Group.GroupRequest;
import com.example.live_backend.dto.Group.GroupResponse;
import com.example.live_backend.model.User.Group;
import com.example.live_backend.model.User.User;

@Component
public class GroupMapper {

    public GroupResponse toResponse(Group group) {
        GroupResponse response = new GroupResponse();
        response.setId(group.getId());
        response.setName(group.getName());
        return response;
    }

    public Group toEntity(GroupRequest request, User creator) {
        Group group = new Group();
        group.setName(request.getName());
        group.setCreator(creator);
        return group;
    }

    public Group updateEntity(Group group, GroupRequest request) {
        group.setName(request.getName());
        return group;
    }
}
