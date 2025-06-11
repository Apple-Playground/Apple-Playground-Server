package com.apple.appleplayground.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String githubId;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column
    private String name;
    
    @Column
    private String avatarUrl;
    
    @Column
    private String location;
    
    @Column
    private String company;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column
    private String blog;
    
    // 추가된 연락처 정보
    @Column
    private String phoneNumber;
    
    @Column
    private String contact1;
    
    @Column
    private String contact2;
    
    @Column
    private String contact3;
    
    @Column
    private String contact4;
    
    @Column
    private String linkedinUrl;
    
    @Column
    private String githubProfileUrl;
    
    // 팔로우 관련 캐시 컬럼
    @Column
    private Integer followersCount = 0;
    
    @Column
    private Integer followingCount = 0;
    
    @Column
    private Integer publicRepos;
    
    @Column
    private Integer followers;
    
    @Column
    private Integer following;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public void updateProfile(String name, String email, String avatarUrl, String location, 
                             String company, String bio, String blog, Integer publicRepos,
                             Integer followers, Integer following) {
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.location = location;
        this.company = company;
        this.bio = bio;
        this.blog = blog;
        this.publicRepos = publicRepos;
        this.followers = followers;
        this.following = following;
    }
    
    public void updateContactInfo(String phoneNumber, String contact1, String contact2, 
                                 String contact3, String contact4, String linkedinUrl, 
                                 String githubProfileUrl) {
        this.phoneNumber = phoneNumber;
        this.contact1 = contact1;
        this.contact2 = contact2;
        this.contact3 = contact3;
        this.contact4 = contact4;
        this.linkedinUrl = linkedinUrl;
        this.githubProfileUrl = githubProfileUrl;
    }
    
    public void incrementFollowersCount() {
        this.followersCount++;
    }
    
    public void decrementFollowersCount() {
        if (this.followersCount > 0) {
            this.followersCount--;
        }
    }
    
    public void incrementFollowingCount() {
        this.followingCount++;
    }
    
    public void decrementFollowingCount() {
        if (this.followingCount > 0) {
            this.followingCount--;
        }
    }
}
