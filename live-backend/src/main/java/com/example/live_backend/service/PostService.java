package com.example.live_backend.service;

import com.example.live_backend.repository.PostRepository;
import com.example.live_backend.repository.UserRepository;
import com.example.live_backend.model.Post;
import com.example.live_backend.model.User;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<Post> getFollowingFeed(String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> new UsernameNotFoundException(currentUsername));

        // The users that the current user is following
        Set<User> usersIFollow = currentUser.getFollowing();

        if (usersIFollow.isEmpty()) {
            return Collections.emptyList();
        }

        // Query posts where author is in the set of followed users
        return postRepository.findByUsers(usersIFollow);
    }

    // For the "explore" feed
    public List<Post> getExploreFeed() {
        return postRepository.findPublicPostsOrderByCreatedAtDesc();
    }

    // or for trending:
    public List<Post> getTrendingFeed() {
        return postRepository.findPublicPostsOrderByLikeCountDesc();
    }
}