package com.example.live_backend.repository.Social;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.live_backend.model.Social.Post;
import com.example.live_backend.model.User.User;

import java.util.Set;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 1) Fetch public posts (for explore)
    @Query("SELECT p FROM Post p WHERE p.isPublic = TRUE ORDER BY p.createdAt DESC")
    List<Post> findPublicPostsOrderByCreatedAtDesc();

    // 2) Fetch posts by a list of users
    @Query("SELECT p FROM Post p WHERE p.user IN :users ORDER BY p.createdAt DESC")
    List<Post> findByUsers(@Param("users") Set<User> users);

    // 3) Possibly a method for trending:
    @Query("SELECT p FROM Post p WHERE p.isPublic = TRUE ORDER BY p.likeCount DESC")
    List<Post> findPublicPostsOrderByLikeCountDesc();
}