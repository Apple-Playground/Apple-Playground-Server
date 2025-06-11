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
}
