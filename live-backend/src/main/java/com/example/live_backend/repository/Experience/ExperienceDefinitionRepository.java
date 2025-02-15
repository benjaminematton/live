package com.example.live_backend.repository.Experience;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.live_backend.model.Experience.ExperienceDefinition;

import java.util.List;
@Repository
public interface ExperienceDefinitionRepository extends JpaRepository<ExperienceDefinition, Long> {
    // Basic CRUD operations are automatically provided by JpaRepository
    
    // Some useful custom query methods you might want:
    List<ExperienceDefinition> findByTitle(String title);
    List<ExperienceDefinition> findByLocation(String location);
    List<ExperienceDefinition> findByAverageRatingGreaterThan(double rating);
    List<ExperienceDefinition> findByOrderByPopularityDesc();
} 