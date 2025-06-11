package com.apple.appleplayground.domain.auth.dto;

import com.apple.appleplayground.domain.auth.entity.Role;
import com.apple.appleplayground.domain.auth.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class UserPrincipal implements OAuth2User {
    
    private final Long id;
    private final String githubId;
    private final String username;
    private final String email;
    private final String name;
    private final String avatarUrl;
    private final Role role;
    private Map<String, Object> attributes;
    
    private UserPrincipal(Long id, String githubId, String username, String email, 
                         String name, String avatarUrl, Role role, Map<String, Object> attributes) {
        this.id = id;
        this.githubId = githubId;
        this.username = username;
        this.email = email;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.attributes = attributes;
    }
    
    public static UserPrincipal create(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getGithubId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getAvatarUrl(),
                user.getRole(),
                Collections.emptyMap()
        );
    }
    
    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        return new UserPrincipal(
                user.getId(),
                user.getGithubId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getAvatarUrl(),
                user.getRole(),
                attributes
        );
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    // OAuth2User의 기본 식별자 메서드
    public String getUsername() {
        return username;
    }
    
    public String getEmail() {
        return email;
    }
}
