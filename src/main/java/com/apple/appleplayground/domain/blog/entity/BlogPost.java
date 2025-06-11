package com.apple.appleplayground.domain.blog.entity;

import com.apple.appleplayground.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 블로그 포스트 엔티티
 */
@Entity
@Table(
    name = "blog_posts",
    indexes = {
        @Index(name = "idx_author_id", columnList = "author_id"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_author_created", columnList = "author_id, created_at")
    }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BlogPost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 500)
    private String title;
    
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer likeCount = 0;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 조회수 증가
     */
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    /**
     * 좋아요 증가
     */
    public void incrementLikeCount() {
        this.likeCount++;
    }
    
    /**
     * 좋아요 감소 (0 이하로 내려가지 않도록)
     */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
    
    /**
     * 포스트 내용 수정
     */
    public void updatePost(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    /**
     * 작성자 확인
     */
    public boolean isAuthor(User user) {
        return this.author.getId().equals(user.getId());
    }
}
