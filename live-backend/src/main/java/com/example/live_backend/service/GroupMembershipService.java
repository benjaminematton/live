package com.example.live_backend.service;

import com.example.live_backend.model.User.Group;
import com.example.live_backend.model.User.GroupMembership;
import com.example.live_backend.model.User.User;
import com.example.live_backend.repository.GroupMembershipRepository;
import com.example.live_backend.dto.Group.GroupMembershipRequest;
import com.example.live_backend.dto.Group.GroupMembershipResponse;
import com.example.live_backend.mapper.GroupMembershipMapper;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class GroupMembershipService {

    private final GroupMembershipRepository groupMembershipRepository;

    private final GroupMembershipMapper groupMembershipMapper;

    public List<GroupMembershipResponse> getAllGroupMemberships() {
        return groupMembershipRepository.findAll().stream()
                .map(groupMembershipMapper::toResponse)
                .collect(Collectors.toList());
    }

    public GroupMembershipResponse getGroupMembershipById(Long id) {
        return groupMembershipMapper.toResponse(groupMembershipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GroupMembership not found")));
    }

    public GroupMembershipResponse createGroupMembership(GroupMembershipRequest request, Group group, User user) {
        GroupMembership groupMembership = groupMembershipMapper.toEntity(request, group, user);
        return groupMembershipMapper.toResponse(groupMembershipRepository.save(groupMembership));
    }

    public void deleteGroupMembership(Long id) {
        groupMembershipRepository.deleteById(id);
    }
}
