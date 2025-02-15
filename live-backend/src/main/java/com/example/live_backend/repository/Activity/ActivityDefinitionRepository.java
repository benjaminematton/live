package com.example.live_backend.repository.Activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.live_backend.model.Activity.ActivityDefinition;

@Repository
public interface ActivityDefinitionRepository extends JpaRepository<ActivityDefinition, Long> {
    ActivityDefinition findByTitle(String title);

    ActivityDefinition findByTitleAndLocation(String title, String location);
} 