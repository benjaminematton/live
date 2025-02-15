package com.example.live_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;
import lombok.RequiredArgsConstructor;

import com.example.live_backend.model.Achievement;
import com.example.live_backend.model.Activity.ActivityDefinition;
import com.example.live_backend.model.Experience.ExperienceDefinition;
import com.example.live_backend.repository.AchievementRepository;
import com.example.live_backend.repository.Activity.ActivityDefinitionRepository;
import com.example.live_backend.repository.Experience.ExperienceDefinitionRepository;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AchievementRepository achievementRepository;
    private final ActivityDefinitionRepository activityDefinitionRepository;
    private final ExperienceDefinitionRepository experienceDefinitionRepository;

    @Override
    public void run(String... args) {
        seedAchievements();
        seedActivityDefinitions();
        seedExperienceDefinitions();
    }

    private void seedAchievements() {
        if (achievementRepository.count() == 0) {
            achievementRepository.saveAll(List.of(
                new Achievement("FIRST_POST", "First Post", "Post at least once", 10),
                new Achievement("SERIAL_POSTER_5", "5 Posts", "Post 5 times", 20),
                new Achievement("SERIAL_POSTER_10", "10 Posts", "Post 10 times", 30),
                new Achievement("SERIAL_POSTER_25", "25 Posts", "Post 25 times", 40),
                new Achievement("SERIAL_POSTER_50", "50 Posts", "Post 50 times", 50),
                new Achievement("SERIAL_POSTER_100", "100 Posts", "Post 100 times", 60),
                new Achievement("NEW_CITY", "New City", "Visit a new city", 70),
                new Achievement("3_PLACES", "3 Places", "Visit 3 unique places", 10),
                new Achievement("10_PLACES", "10 Places", "Visit 10 unique places", 20),
                new Achievement("25_PLACES", "25 Places", "Visit 25 unique places", 30),
                new Achievement("50_PLACES", "50 Places", "Visit 50 unique places", 40),
                new Achievement("100_PLACES", "100 Places", "Visit 100 unique places", 50),
                new Achievement("NEW_COUNTRY", "New Country", "Visit a new country", 60),
                new Achievement("INVITE_FRIEND", "Invite a Friend", "Invite a friend to join the app", 70),
                new Achievement("MUTLIPLE_ACTIVITIES", "Do Multiple Activities", "Do multiple activities", 80)
                // etc.
            ));
            System.out.println("Achievements seeded.");
        }
    }

    private void seedActivityDefinitions() {
        if (activityDefinitionRepository.count() == 0) {
            ActivityDefinition a1 = new ActivityDefinition();
            a1.setTitle("Knight's Steakhouse - Downtown Ann Arbor");
            a1.setLocation("600 E Liberty St, Ann Arbor, MI 48104");
            activityDefinitionRepository.save(a1);
            System.out.println("ActivityDefinitions seeded.");

            ActivityDefinition a2 = new ActivityDefinition();
            a2.setTitle("The Mitten Brewing Company - Ann Arbor");
            a2.setLocation("210 S Main St, Ann Arbor, MI 48104");
            activityDefinitionRepository.save(a2);
            System.out.println("ActivityDefinitions seeded.");

            ActivityDefinition a3 = new ActivityDefinition();
            a3.setTitle("Mash Whisky Bar");
            a3.setLocation("207 E Washington St, Ann Arbor, MI 48104");
            activityDefinitionRepository.save(a3);
            System.out.println("ActivityDefinitions seeded.");
        }
    }

    private void seedExperienceDefinitions() {
        if (experienceDefinitionRepository.count() == 0) {

            ActivityDefinition a1 = activityDefinitionRepository.findByTitleAndLocation("Knight's Steakhouse - Downtown Ann Arbor", "600 E Liberty St, Ann Arbor, MI 48104");   
            ActivityDefinition a2 = activityDefinitionRepository.findByTitleAndLocation("The Mitten Brewing Company - Ann Arbor", "210 S Main St, Ann Arbor, MI 48104");
            ActivityDefinition a3 = activityDefinitionRepository.findByTitleAndLocation("Mash Whisky Bar", "207 E Washington St, Ann Arbor, MI 48104");

            ExperienceDefinition e1 = new ExperienceDefinition();
            e1.setTitle("Knight's Steakhouse - Downtown Ann Arbor + The Mitten Brewing Company - Ann Arbor");
            e1.setActivities(List.of(a1, a2));
            experienceDefinitionRepository.save(e1);
            System.out.println("ExperienceDefinitions seeded.");

            ExperienceDefinition e2 = new ExperienceDefinition();
            e2.setTitle("Mash Whisky Bar");
            e2.setActivities(List.of(a3));
            experienceDefinitionRepository.save(e2);
            System.out.println("ExperienceDefinitions seeded.");

            ExperienceDefinition e3 = new ExperienceDefinition();
            e3.setTitle("Knight's Steakhouse - Downtown Ann Arbor + Mash Whisky Bar");
            e3.setActivities(List.of(a1, a3));
            experienceDefinitionRepository.save(e3);
            System.out.println("ExperienceDefinitions seeded.");
        }
    }
}

