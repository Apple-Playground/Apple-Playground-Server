package com.apple.appleplayground.domain.follow.controller;

import com.apple.appleplayground.domain.auth.dto.UserPrincipal;
import com.apple.appleplayground.domain.follow.dto.response.FollowListResponse;
import com.apple.appleplayground.domain.follow.dto.response.FollowStatusResponse;
import com.apple.appleplayground.domain.follow.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 팔로우 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
@Tag(name = "Follow API", description = "팔로우 관련 API")
public class FollowController {
    
    private final FollowService followService;
    
    @Operation(summary = "사용자 팔로우", description = "지정된 사용자를 팔로우합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "팔로우 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (자기 자신 팔로우, 이미 팔로우한 사용자 등)"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자")
    })
    @PostMapping("/{userId}")
    public ResponseEntity<FollowStatusResponse> followUser(
            @Parameter(description = "팔로우할 사용자 ID") @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        FollowStatusResponse response = followService.followUser(currentUser.getId(), userId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "사용자 언팔로우", description = "지정된 사용자를 언팔로우합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "언팔로우 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (팔로우하지 않은 사용자)"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<FollowStatusResponse> unfollowUser(
            @Parameter(description = "언팔로우할 사용자 ID") @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        FollowStatusResponse response = followService.unfollowUser(currentUser.getId(), userId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "팔로우 상태 확인", description = "지정된 사용자와의 팔로우 상태를 확인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "상태 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자")
    })
    @GetMapping("/status/{userId}")
    public ResponseEntity<FollowStatusResponse> getFollowStatus(
            @Parameter(description = "대상 사용자 ID") @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        FollowStatusResponse response = followService.getFollowStatus(currentUser.getId(), userId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "팔로워 목록 조회", description = "지정된 사용자의 팔로워 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "팔로워 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자")
    })
    @GetMapping("/followers/{userId}")
    public ResponseEntity<FollowListResponse> getFollowers(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        FollowListResponse response = followService.getFollowers(userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "팔로잉 목록 조회", description = "지정된 사용자의 팔로잉 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "팔로잉 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자")
    })
    @GetMapping("/following/{userId}")
    public ResponseEntity<FollowListResponse> getFollowing(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        FollowListResponse response = followService.getFollowing(userId, pageable);
        return ResponseEntity.ok(response);
    }
}
