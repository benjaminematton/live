package com.example.live_backend.controller;

import com.example.live_backend.dto.Activity.ActivityDefinitionRequest;
import com.example.live_backend.dto.Activity.ActivityDefinitionResponse;
import com.example.live_backend.mapper.ActivityDefinitionMapper;
import com.example.live_backend.model.Activity.ActivityDefinition;
import com.example.live_backend.repository.Activity.ActivityDefinitionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activityDefinitions")
@CrossOrigin(origins = "*")
public class ActivityDefinitionController {

    @Autowired
    private ActivityDefinitionRepository activityDefinitionRepository;
    
    @Autowired
    private ActivityDefinitionMapper mapper;

    @GetMapping
    public List<ActivityDefinitionResponse> getAllActivities() {
        List<ActivityDefinition> activities = activityDefinitionRepository.findAll();
        return activities.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDefinitionResponse> getActivityDefinitionById(@PathVariable Long id) {
        return activityDefinitionRepository.findById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ActivityDefinitionResponse createActivityDefinition(@Valid @RequestBody ActivityDefinitionRequest request) {
        ActivityDefinition activityDefinition = mapper.toEntity(request);
        ActivityDefinition savedActivityDefinition = activityDefinitionRepository.save(activityDefinition);
        return mapper.toResponse(savedActivityDefinition);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityDefinitionResponse> updateActivityDefinition(
            @PathVariable Long id,
            @Valid @RequestBody ActivityDefinitionRequest request) {
        return activityDefinitionRepository.findById(id)
                .map(existingActivityDefinition -> {
                    mapper.updateEntity(existingActivityDefinition, request);
                    ActivityDefinition updatedActivityDefinition = activityDefinitionRepository.save(existingActivityDefinition);
                    return ResponseEntity.ok(mapper.toResponse(updatedActivityDefinition));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivityDefinition(@PathVariable Long id) {
        return activityDefinitionRepository.findById(id)
                .map(activityDefinition -> {
                    activityDefinitionRepository.delete(activityDefinition  );
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/title/{title}")
    public ActivityDefinitionResponse findByTitle(@PathVariable String title) {
        return mapper.toResponse(activityDefinitionRepository.findByTitle(title));
    }

    // Search activities by title and location
    @GetMapping("/search")
    public ActivityDefinitionResponse findByTitleAndLocation(
            @RequestParam String title,
            @RequestParam String location) {
        return mapper.toResponse(activityDefinitionRepository.findByTitleAndLocation(title, location));
    }
} 