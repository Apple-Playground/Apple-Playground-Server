package com.apple.appleplayground.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 정보 응답 DTO")
public class UserResponseDto {
    
    @Schema(description = "사용자 ID", example = "1")
    private Long id;
    
    @Schema(description = "GitHub ID", example = "123456789")
    private String githubId;
    
    @Schema(description = "GitHub 사용자명", example = "octocat")
    private String username;
    
    @Schema(description = "이메일 주소", example = "octocat@github.com")
    private String email;
    
    @Schema(description = "이름", example = "The Octocat")
    private String name;
    
    @Schema(description = "프로필 이미지 URL", example = "https://github.com/images/error/octocat_happy.gif")
    private String avatarUrl;
    
    @Schema(description = "위치", example = "San Francisco, CA")
    private String location;
    
    @Schema(description = "회사", example = "GitHub")
    private String company;
    
    @Schema(description = "자기소개", example = "A passionate developer")
    private String bio;
    
    @Schema(description = "블로그 URL", example = "https://github.blog")
    private String blog;
    
    @Schema(description = "공개 리포지토리 수", example = "2")
    private Integer publicRepos;
    
    @Schema(description = "팔로워 수", example = "20")
    private Integer followers;
    
    @Schema(description = "팔로잉 수", example = "0")
    private Integer following;
    
    @Schema(description = "사용자 역할", example = "USER")
    private String role;
}
