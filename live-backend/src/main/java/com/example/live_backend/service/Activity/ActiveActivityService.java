package com.example.live_backend.service.Activity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.live_backend.model.Activity.ActiveActivity;
import com.example.live_backend.model.Activity.Activity;
import com.example.live_backend.model.Experience.ActiveExperience;
import com.example.live_backend.repository.Activity.ActiveActivityRepository;
import com.example.live_backend.service.PhotoStorageService;
import com.example.live_backend.service.Experience.ActiveExperienceService;
import com.example.live_backend.dto.CheckInRequest;
import com.example.live_backend.dto.Activity.ActiveActivityResponse;
import com.example.live_backend.mapper.ActiveActivityMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActiveActivityService {
    private final ActiveActivityRepository activeActivityRepository;
    private final ActiveExperienceService activeExperienceService;
    private final PhotoStorageService photoStorageService;
    private final ActiveActivityMapper activeActivityMapper;

    @Transactional
    public ActiveActivityResponse startActivity(ActiveExperience activeExperience, Activity activity) {
        // Create the ActiveActivity
        ActiveActivity activeActivity = new ActiveActivity();
        activeActivity.setActiveExperience(activeExperience);
        activeActivity.setActivity(activity);

        activeActivity.setStartTime(LocalDateTime.now());
        activeActivity.setPhotoPromptTime(generateRandomPromptTime(LocalDateTime.now()));

        activeActivityRepository.save(activeActivity);
        return activeActivityMapper.toResponse(activeActivity);
    }

    @Transactional
    public void submitActivityPhoto(Long activeActivityId, MultipartFile photoFile) {
        ActiveActivity aa = activeActivityRepository.findById(activeActivityId)
            .orElseThrow(() -> new RuntimeException("ActiveActivity not found"));

        // Save the photo somewhere and store the URL
        String photoUrl = photoStorageService.savePhoto(photoFile);

        aa.setPhotoUrl(photoUrl);
        aa.setPhotoSubmitted(true);
        activeActivityRepository.save(aa);
    }

    private LocalDateTime generateRandomPromptTime(LocalDateTime activityStartTime) {
        // The random prompt is between activityStartTime and 20 mins after
        LocalDateTime maxTime = activityStartTime.plusMinutes(20);

        long startMillis = activityStartTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endMillis = maxTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        long randomMillis = ThreadLocalRandom.current().nextLong(startMillis, endMillis);
        return Instant.ofEpochMilli(randomMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }

    public ActiveActivityResponse checkInActivity(Long activeActivityId, CheckInRequest request, Long activeExperienceId) {
        ActiveActivity activeActivity = activeActivityRepository.findById(activeActivityId)
            .orElseThrow(() -> new RuntimeException("ActiveActivity not found"));

        ActiveExperience activeExperience = activeExperienceService.getActiveExperience(activeExperienceId);

        double lat = request.getLatitude();
        double lon = request.getLongitude();

        Activity activity = activeActivity.getActivity();
        double dist = distance(lat, lon, activity.getLatitude(), activity.getLongitude());
        if (dist <= 10) {
            return startActivity(activeExperience, activity);
        } else {
            throw new RuntimeException("You are too far from the location!");
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        // Earth radius in meters
        double R = 6371e3;
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
                Math.cos(phi1) * Math.cos(phi2) *
                Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = R * c;
        return dist;
    }
}
