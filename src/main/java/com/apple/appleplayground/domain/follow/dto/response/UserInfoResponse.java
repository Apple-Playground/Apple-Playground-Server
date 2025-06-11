package com.apple.appleplayground.domain.follow.dto.response;

import com.apple.appleplayground.domain.auth.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자 정보 응답 DTO (팔로우 관련)
 */
@Getter
@Builder
@Schema(description = "사용자 정보 응답")
public class UserInfoResponse {
    
    @Schema(description = "사용자 ID", example = "1")
    private Long id;
    
    @Schema(description = "사용자명", example = "john_doe")
    private String username;
    
    @Schema(description = "이메일", example = "john@example.com")
    private String email;
    
    @Schema(description = "이름", example = "John Doe")
    private String name;
    
    @Schema(description = "아바타 URL", example = "https://avatars.githubusercontent.com/u/123456?v=4")
    private String avatarUrl;
    
    @Schema(description = "위치", example = "Seoul, South Korea")
    private String location;
    
    @Schema(description = "회사", example = "Apple Inc.")
    private String company;
    
    @Schema(description = "소개", example = "Software Engineer")
    private String bio;
    
    @Schema(description = "블로그 URL", example = "https://blog.example.com")
    private String blog;
    
    @Schema(description = "팔로워 수", example = "150")
    private Integer followersCount;
    
    @Schema(description = "팔로잉 수", example = "75")
    private Integer followingCount;
    
    @Schema(description = "가입일시", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAt;
    
    /**
     * User 엔티티로부터 UserInfoResponse 생성
     */
    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .location(user.getLocation())
                .company(user.getCompany())
                .bio(user.getBio())
                .blog(user.getBlog())
                .followersCount(user.getFollowersCount())
                .followingCount(user.getFollowingCount())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
