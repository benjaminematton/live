package com.example.live_backend.controller;

import com.example.live_backend.model.Experience.ExperienceDefinition;
import com.example.live_backend.repository.Experience.ExperienceDefinitionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experienceDefinitions")
@CrossOrigin(origins = "*")
public class ExperienceDefinitionController {

    @Autowired
    private ExperienceDefinitionRepository experienceDefinitionRepository;

    // Get all experiences
    @GetMapping
    public List<ExperienceDefinition> getAllExperienceDefinitions() {
        return experienceDefinitionRepository.findAll();
    }

    // Get experience by ID
    @GetMapping("/{id}")
    public ResponseEntity<ExperienceDefinition> getExperienceDefinitionById(@PathVariable Long id) {
        return experienceDefinitionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new experience
    @PostMapping
    public ExperienceDefinition createExperienceDefinition(@RequestBody ExperienceDefinition experienceDefinition) {
            return experienceDefinitionRepository.save(experienceDefinition);
    }

    // Update experience
    @PutMapping("/{id}")
    public ResponseEntity<ExperienceDefinition> updateExperienceDefinition(@PathVariable Long id, 
                                                               @RequestBody ExperienceDefinition experienceDefinitionDetails) {
        return experienceDefinitionRepository.findById(id)
                .map(existingExperienceDefinition -> {
                    existingExperienceDefinition.setTitle(experienceDefinitionDetails.getTitle());
                    // Set other fields as needed
                    ExperienceDefinition updatedExperienceDefinition = experienceDefinitionRepository.save(existingExperienceDefinition);
                    return ResponseEntity.ok(updatedExperienceDefinition);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete experience
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExperienceDefinition(@PathVariable Long id) {
        return experienceDefinitionRepository.findById(id)
                .map(experienceDefinition -> {
                    experienceDefinitionRepository.delete(experienceDefinition);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Additional custom endpoints based on repository methods
    @GetMapping("/search/title/{title}")
    public List<ExperienceDefinition> findByTitle(@PathVariable String title) {
            return experienceDefinitionRepository.findByTitle(title);
    }

    @GetMapping("/search/location/{location}")
    public List<ExperienceDefinition> findByLocation(@PathVariable String location) {
        return experienceDefinitionRepository.findByLocation(location);
    }

    @GetMapping("/search/rating/{rating}")
    public List<ExperienceDefinition> findByRating(@PathVariable double rating) {
        return experienceDefinitionRepository.findByAverageRatingGreaterThan(rating);
    }

    @GetMapping("/popular")
    public List<ExperienceDefinition> findPopular() {
        return experienceDefinitionRepository.findByOrderByPopularityDesc();
    }
} 