package com.apple.appleplayground.domain.blog.controller;

import com.apple.appleplayground.domain.auth.dto.UserPrincipal;
import com.apple.appleplayground.domain.blog.dto.request.CreateBlogPostRequest;
import com.apple.appleplayground.domain.blog.dto.request.UpdateBlogPostRequest;
import com.apple.appleplayground.domain.blog.dto.response.BlogPostListResponse;
import com.apple.appleplayground.domain.blog.dto.response.BlogPostResponse;
import com.apple.appleplayground.domain.blog.service.BlogPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 블로그 포스트 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
@Tag(name = "Blog API", description = "블로그 포스트 관련 API")
public class BlogController {
    
    private final BlogPostService blogPostService;
    
    @Operation(summary = "블로그 포스트 생성", description = "새로운 블로그 포스트를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "포스트 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping("/posts")
    public ResponseEntity<BlogPostResponse> createPost(
            @Valid @RequestBody CreateBlogPostRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        BlogPostResponse response = blogPostService.createPost(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(summary = "블로그 포스트 상세 조회", description = "지정된 ID의 블로그 포스트를 조회합니다. 조회수가 증가합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "포스트 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 포스트")
    })
    @GetMapping("/posts/{postId}")
    public ResponseEntity<BlogPostResponse> getPost(
            @Parameter(description = "포스트 ID") @PathVariable Long postId) {
        
        BlogPostResponse response = blogPostService.getPost(postId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "전체 블로그 포스트 목록 조회", description = "모든 사용자의 블로그 포스트를 최신순으로 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "포스트 목록 조회 성공")
    })
    @GetMapping("/posts")
    public ResponseEntity<BlogPostListResponse> getAllPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        BlogPostListResponse response = blogPostService.getAllPosts(pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "특정 사용자의 블로그 포스트 목록 조회", description = "지정된 사용자의 블로그 포스트를 최신순으로 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "포스트 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자")
    })
    @GetMapping("/posts/user/{userId}")
    public ResponseEntity<BlogPostListResponse> getUserPosts(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        BlogPostListResponse response = blogPostService.getUserPosts(userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "블로그 포스트 검색", description = "제목 또는 내용에서 키워드를 검색합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/posts/search")
    public ResponseEntity<BlogPostListResponse> searchPosts(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        BlogPostListResponse response = blogPostService.searchPosts(keyword, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "블로그 포스트 수정", description = "자신이 작성한 블로그 포스트를 수정합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "포스트 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 포스트")
    })
    @PutMapping("/posts/{postId}")
    public ResponseEntity<BlogPostResponse> updatePost(
            @Parameter(description = "포스트 ID") @PathVariable Long postId,
            @Valid @RequestBody UpdateBlogPostRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        BlogPostResponse response = blogPostService.updatePost(postId, currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "블로그 포스트 삭제", description = "자신이 작성한 블로그 포스트를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "포스트 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 포스트")
    })
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "포스트 ID") @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        blogPostService.deletePost(postId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "블로그 포스트 좋아요 증가", description = "지정된 포스트의 좋아요를 1 증가시킵니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "좋아요 증가 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 포스트")
    })
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<BlogPostResponse> incrementLike(
            @Parameter(description = "포스트 ID") @PathVariable Long postId) {
        
        BlogPostResponse response = blogPostService.incrementLike(postId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "블로그 포스트 좋아요 감소", description = "지정된 포스트의 좋아요를 1 감소시킵니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "좋아요 감소 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 포스트")
    })
    @PostMapping("/posts/{postId}/unlike")
    public ResponseEntity<BlogPostResponse> decrementLike(
            @Parameter(description = "포스트 ID") @PathVariable Long postId) {
        
        BlogPostResponse response = blogPostService.decrementLike(postId);
        return ResponseEntity.ok(response);
    }
}
