package com.example.live_backend.service;

import com.example.live_backend.model.ExperienceShare;
import com.example.live_backend.model.User;
import com.example.live_backend.repository.ExperienceShareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.live_backend.model.Experience;
import com.example.live_backend.repository.ExperienceRepository;
import com.example.live_backend.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperienceShareService {
    private final ExperienceShareRepository experienceShareRepository;
    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    @Transactional
    public void shareExperience(Long experienceId, String ownerUsername, List<String> targetUsernames) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Experience not found"));
        
        for (String username : targetUsernames) {
            User targetUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            ExperienceShare share = new ExperienceShare();
            share.setExperience(experience);
            share.setSharedWith(targetUser);
            experienceShareRepository.save(share);
        }
    }

    @Transactional
    public void unshareExperience(Long experienceId, String ownerUsername, String targetUsername) {
        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + targetUsername));
        experienceShareRepository.deleteByExperienceIdAndSharedWithId(experienceId, targetUser.getId());
    }

    public List<Experience> getSharedExperiences(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                
        return experienceShareRepository.findBySharedWithId(user.getId()).stream()
                .map(ExperienceShare::getExperience)
                .collect(Collectors.toList());
    }

    public List<Experience> getSharedExperiencesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return experienceShareRepository.findBySharedWithId(user.getId()).stream()
                .map(ExperienceShare::getExperience)
                .collect(Collectors.toList());
    }
}
