package com.example.live_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    @Column(length = 1000)
    private String profilePicture;

    private String bio;

    /**
     * The set of users that *this* user is following.
     * 
     * We define a join table called 'user_following' with:
     *  - follower_id pointing to this user
     *  - followee_id pointing to the user being followed
     */
    @ManyToMany
    @JoinTable(
        name = "user_following",
        joinColumns = @JoinColumn(name = "follower_id"),
        inverseJoinColumns = @JoinColumn(name = "followee_id")
    )
    private Set<User> following = new HashSet<>();
    /**
     * The set of users who follow *this* user.
     * 
     * 'mappedBy = "following"' tells JPA that
     * the 'followers' relationship is the inverse side
     * of the 'following' relationship.
     */
    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    private List<ExperienceShare> sharedExperiences = new ArrayList<>();

    private List<Experience> userExperiences = new ArrayList<>();

    private boolean prefersDarkMode = false;

    /**
     * Checks if this user is friends with 'otherUser' by verifying
     * that both follow each other.
     */
    public boolean isFriendsWith(User otherUser) {
        return this.following.contains(otherUser) 
            && this.followers.contains(otherUser);
    }

    /**
     * Returns all users with whom this user is "friends" (mutual following).
     */
    public Set<User> getFriends() {
        Set<User> mutual = new HashSet<>(this.following);
        mutual.retainAll(this.followers);  // Intersection
        return mutual;
    }

}
