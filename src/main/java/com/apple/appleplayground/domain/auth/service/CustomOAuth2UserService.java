package com.apple.appleplayground.domain.auth.service;

import com.apple.appleplayground.domain.auth.client.GitHubApiClient;
import com.apple.appleplayground.domain.auth.dto.GitHubUserDto;
import com.apple.appleplayground.domain.auth.entity.Role;
import com.apple.appleplayground.domain.auth.entity.User;
import com.apple.appleplayground.domain.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final UserRepository userRepository;
    private final GitHubApiClient gitHubApiClient;
    
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("GitHub OAuth2 user loading started");
        
        // 기본 OAuth2User 정보 가져오기
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        try {
            // GitHub API를 통해 추가 사용자 정보 가져오기
            String accessToken = userRequest.getAccessToken().getTokenValue();
            GitHubUserDto githubUserDto = gitHubApiClient.getUser("Bearer " + accessToken);
            
            // 이메일 정보가 없는 경우 별도 API 호출
            if (githubUserDto.getEmail() == null) {
                GitHubUserDto.Email[] emails = gitHubApiClient.getUserEmails("Bearer " + accessToken);
                for (GitHubUserDto.Email email : emails) {
                    if (email.isPrimary() && email.isVerified()) {
                        githubUserDto = GitHubUserDto.builder()
                                .id(githubUserDto.getId())
                                .username(githubUserDto.getUsername())
                                .name(githubUserDto.getName())
                                .email(email.getEmail())
                                .avatarUrl(githubUserDto.getAvatarUrl())
                                .location(githubUserDto.getLocation())
                                .company(githubUserDto.getCompany())
                                .bio(githubUserDto.getBio())
                                .blog(githubUserDto.getBlog())
                                .publicRepos(githubUserDto.getPublicRepos())
                                .followers(githubUserDto.getFollowers())
                                .following(githubUserDto.getFollowing())
                                .createdAt(githubUserDto.getCreatedAt())
                                .updatedAt(githubUserDto.getUpdatedAt())
                                .build();
                        break;
                    }
                }
            }
            
            // 사용자 정보 저장 또는 업데이트
            User user = saveOrUpdateUser(githubUserDto);
            
            // CustomOAuth2User 반환
            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                    createAttributes(user, oauth2User.getAttributes()),
                    "id"
            );
            
        } catch (Exception e) {
            log.error("Error during GitHub OAuth2 authentication", e);
            throw new OAuth2AuthenticationException("GitHub OAuth2 authentication failed");
        }
    }
    
    private User saveOrUpdateUser(GitHubUserDto githubUserDto) {
        String githubId = String.valueOf(githubUserDto.getId());
        
        return userRepository.findByGithubId(githubId)
                .map(existingUser -> {
                    existingUser.updateProfile(
                            githubUserDto.getName(),
                            githubUserDto.getEmail(),
                            githubUserDto.getAvatarUrl(),
                            githubUserDto.getLocation(),
                            githubUserDto.getCompany(),
                            githubUserDto.getBio(),
                            githubUserDto.getBlog(),
                            githubUserDto.getPublicRepos(),
                            githubUserDto.getFollowers(),
                            githubUserDto.getFollowing()
                    );
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .githubId(githubId)
                            .username(githubUserDto.getUsername())
                            .email(githubUserDto.getEmail())
                            .name(githubUserDto.getName())
                            .avatarUrl(githubUserDto.getAvatarUrl())
                            .location(githubUserDto.getLocation())
                            .company(githubUserDto.getCompany())
                            .bio(githubUserDto.getBio())
                            .blog(githubUserDto.getBlog())
                            .publicRepos(githubUserDto.getPublicRepos())
                            .followers(githubUserDto.getFollowers())
                            .following(githubUserDto.getFollowing())
                            .role(Role.USER)
                            .build();
                    return userRepository.save(newUser);
                });
    }
    
    private Map<String, Object> createAttributes(User user, Map<String, Object> originalAttributes) {
        Map<String, Object> attributes = new java.util.HashMap<>(originalAttributes);
        attributes.put("userId", user.getId());
        attributes.put("username", user.getUsername());
        attributes.put("email", user.getEmail());
        attributes.put("role", user.getRole().name());
        return attributes;
    }
}
