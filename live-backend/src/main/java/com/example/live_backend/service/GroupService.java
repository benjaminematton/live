package com.example.live_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import com.example.live_backend.dto.Group.GroupRequest;
import com.example.live_backend.dto.Group.GroupResponse;
import com.example.live_backend.mapper.GroupMapper;
import com.example.live_backend.model.User.Group;
import com.example.live_backend.model.User.User;
import com.example.live_backend.repository.GroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    @Transactional
    public GroupResponse createGroup(GroupRequest group, User creator) {
        return groupMapper.toResponse(groupRepository.save(groupMapper.toEntity(group, creator)));
    }

    public GroupResponse getGroupByName(String name) {
        return groupMapper.toResponse(groupRepository.findByName(name));
    }

    public GroupResponse getGroupById(Long id) {
        return groupMapper.toResponse(groupRepository.findById(id).orElse(null));
    }

    public List<GroupResponse> getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        return groups.stream()
                .map(groupMapper::toResponse)
                .collect(Collectors.toList());
    }
}
