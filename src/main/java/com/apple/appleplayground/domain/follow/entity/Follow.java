package com.apple.appleplayground.domain.follow.entity;

import com.apple.appleplayground.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 팔로우 관계를 나타내는 엔티티
 * follower가 following을 팔로우하는 관계
 */
@Entity
@Table(
    name = "follows",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_follow_follower_following", 
        columnNames = {"follower_id", "following_id"}
    ),
    indexes = {
        @Index(name = "idx_follower_id", columnList = "follower_id"),
        @Index(name = "idx_following_id", columnList = "following_id")
    }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Follow {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 팔로우를 하는 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;
    
    /**
     * 팔로우를 받는 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 팔로우 관계 생성
     */
    public static Follow create(User follower, User following) {
        return Follow.builder()
                .follower(follower)
                .following(following)
                .build();
    }
}
