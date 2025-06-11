package com.apple.appleplayground.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubUserDto {
    
    private Long id;
    
    @JsonProperty("login")
    private String username;
    
    private String name;
    
    private String email;
    
    @JsonProperty("avatar_url")
    private String avatarUrl;
    
    private String location;
    
    private String company;
    
    private String bio;
    
    private String blog;
    
    @JsonProperty("public_repos")
    private Integer publicRepos;
    
    private Integer followers;
    
    private Integer following;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Email {
        private String email;
        private boolean primary;
        private boolean verified;
        private String visibility;
    }
}
