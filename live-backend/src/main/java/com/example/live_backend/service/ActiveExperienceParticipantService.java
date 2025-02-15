package com.example.live_backend.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.live_backend.model.User.ActiveExperienceParticipant;
import com.example.live_backend.repository.User.ActiveExperienceParticipantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActiveExperienceParticipantService {
    private final ActiveExperienceParticipantRepository participantRepository;

    public void updateUserLocation(Long activeExperienceId, Long userId, Double lat, Double lon) {
        ActiveExperienceParticipant participant = participantRepository.findByActiveExperienceIdAndUserId(activeExperienceId, userId)
            .orElseThrow(() -> new RuntimeException("User not in this active experience"));

        participant.setLatitude(lat);
        participant.setLongitude(lon);
        participant.setLastLocationUpdate(LocalDateTime.now());

        participantRepository.save(participant);
    }
}
