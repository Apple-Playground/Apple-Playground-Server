package com.apple.appleplayground.domain.follow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 팔로우 상태 응답 DTO
 */
@Getter
@Builder
@Schema(description = "팔로우 상태 응답")
public class FollowStatusResponse {
    
    @Schema(description = "팔로우 여부", example = "true")
    private boolean isFollowing;
    
    @Schema(description = "팔로워 수", example = "150")
    private Integer followersCount;
    
    @Schema(description = "팔로잉 수", example = "75")
    private Integer followingCount;
    
    /**
     * 팔로우 상태 응답 생성
     */
    public static FollowStatusResponse of(boolean isFollowing, Integer followersCount, Integer followingCount) {
        return FollowStatusResponse.builder()
                .isFollowing(isFollowing)
                .followersCount(followersCount)
                .followingCount(followingCount)
                .build();
    }
}
