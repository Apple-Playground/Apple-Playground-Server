package com.apple.appleplayground.domain.blog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 블로그 포스트 목록 응답 DTO
 */
@Getter
@Builder
@Schema(description = "블로그 포스트 목록 응답")
public class BlogPostListResponse {
    
    @Schema(description = "블로그 포스트 목록")
    private List<BlogPostResponse> posts;
    
    @Schema(description = "현재 페이지", example = "0")
    private int currentPage;
    
    @Schema(description = "전체 페이지 수", example = "5")
    private int totalPages;
    
    @Schema(description = "전체 요소 수", example = "50")
    private long totalElements;
    
    @Schema(description = "페이지 크기", example = "10")
    private int size;
    
    @Schema(description = "마지막 페이지 여부", example = "false")
    private boolean isLast;
    
    /**
     * Page<BlogPost>로부터 BlogPostListResponse 생성
     */
    public static BlogPostListResponse from(Page<com.apple.appleplayground.domain.blog.entity.BlogPost> postPage) {
        List<BlogPostResponse> posts = postPage.getContent().stream()
                .map(BlogPostResponse::fromSummary)
                .toList();
        
        return BlogPostListResponse.builder()
                .posts(posts)
                .currentPage(postPage.getNumber())
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .size(postPage.getSize())
                .isLast(postPage.isLast())
                .build();
    }
}
