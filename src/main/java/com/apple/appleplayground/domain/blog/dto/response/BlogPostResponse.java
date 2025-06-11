package com.apple.appleplayground.domain.blog.dto.response;

import com.apple.appleplayground.domain.blog.entity.BlogPost;
import com.apple.appleplayground.domain.follow.dto.response.UserInfoResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 블로그 포스트 응답 DTO
 */
@Getter
@Builder
@Schema(description = "블로그 포스트 응답")
public class BlogPostResponse {
    
    @Schema(description = "포스트 ID", example = "1")
    private Long id;
    
    @Schema(description = "포스트 제목", example = "Spring Boot 튜토리얼")
    private String title;
    
    @Schema(description = "포스트 내용", example = "Spring Boot로 REST API를 만드는 방법...")
    private String content;
    
    @Schema(description = "작성자 정보")
    private UserInfoResponse author;
    
    @Schema(description = "조회수", example = "150")
    private Integer viewCount;
    
    @Schema(description = "좋아요 수", example = "25")
    private Integer likeCount;
    
    @Schema(description = "작성일시", example = "2024-01-01T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정일시", example = "2024-01-01T15:45:00")
    private LocalDateTime updatedAt;
    
    /**
     * BlogPost 엔티티로부터 BlogPostResponse 생성
     */
    public static BlogPostResponse from(BlogPost blogPost) {
        return BlogPostResponse.builder()
                .id(blogPost.getId())
                .title(blogPost.getTitle())
                .content(blogPost.getContent())
                .author(UserInfoResponse.from(blogPost.getAuthor()))
                .viewCount(blogPost.getViewCount())
                .likeCount(blogPost.getLikeCount())
                .createdAt(blogPost.getCreatedAt())
                .updatedAt(blogPost.getUpdatedAt())
                .build();
    }
    
    /**
     * BlogPost 엔티티로부터 BlogPostResponse 생성 (내용 미포함 - 목록용)
     */
    public static BlogPostResponse fromSummary(BlogPost blogPost) {
        return BlogPostResponse.builder()
                .id(blogPost.getId())
                .title(blogPost.getTitle())
                .content(blogPost.getContent().length() > 200 ? 
                        blogPost.getContent().substring(0, 200) + "..." : 
                        blogPost.getContent())
                .author(UserInfoResponse.from(blogPost.getAuthor()))
                .viewCount(blogPost.getViewCount())
                .likeCount(blogPost.getLikeCount())
                .createdAt(blogPost.getCreatedAt())
                .updatedAt(blogPost.getUpdatedAt())
                .build();
    }
}
