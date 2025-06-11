package com.apple.appleplayground.domain.auth.service;

import com.apple.appleplayground.domain.auth.dto.UserResponseDto;
import com.apple.appleplayground.domain.auth.entity.User;
import com.apple.appleplayground.domain.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserResponseDto getUserByGithubId(String githubId) {
        User user = userRepository.findByGithubId(githubId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with GitHub ID: " + githubId));
        
        return convertToDto(user);
    }
    
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        return convertToDto(user);
    }
    
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
        
        return convertToDto(user);
    }
    
    private UserResponseDto convertToDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .githubId(user.getGithubId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .location(user.getLocation())
                .company(user.getCompany())
                .bio(user.getBio())
                .blog(user.getBlog())
                .publicRepos(user.getPublicRepos())
                .followers(user.getFollowers())
                .following(user.getFollowing())
                .role(user.getRole().name())
                .build();
    }
}
