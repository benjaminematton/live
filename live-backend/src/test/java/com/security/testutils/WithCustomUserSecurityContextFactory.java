package com.security.testutils;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.live_backend.model.User.User;
import com.example.live_backend.security.CustomUserDetails;

import java.util.Collection;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class WithCustomUserSecurityContextFactory 
    implements WithSecurityContextFactory<WithCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Create a User object first since CustomUserDetails requires it
        User user = new User();
        user.setUsername(annotation.username());
        // Set other user properties as needed

        // Create CustomUserDetails with the User object
        CustomUserDetails customUser = new CustomUserDetails(user);

        // 2. Build the Authentication object with your custom user
        Collection<GrantedAuthority> authorities = Arrays.stream(annotation.roles())
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toList());

        Authentication auth = new UsernamePasswordAuthenticationToken(
            customUser, 
            null, 
            authorities
        );

        // 3. Store in SecurityContext
        context.setAuthentication(auth);
        return context;
    }
}