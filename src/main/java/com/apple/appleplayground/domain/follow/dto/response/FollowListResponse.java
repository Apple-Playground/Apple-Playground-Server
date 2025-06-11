package com.apple.appleplayground.domain.follow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 팔로우 목록 응답 DTO
 */
@Getter
@Builder
@Schema(description = "팔로우 목록 응답")
public class FollowListResponse {
    
    @Schema(description = "사용자 목록")
    private List<UserInfoResponse> users;
    
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
     * Page<User>로부터 FollowListResponse 생성
     */
    public static FollowListResponse from(Page<com.apple.appleplayground.domain.auth.entity.User> userPage) {
        List<UserInfoResponse> users = userPage.getContent().stream()
                .map(UserInfoResponse::from)
                .toList();
        
        return FollowListResponse.builder()
                .users(users)
                .currentPage(userPage.getNumber())
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .size(userPage.getSize())
                .isLast(userPage.isLast())
                .build();
    }
}
