package com.apple.appleplayground.domain.image.controller;

import com.apple.appleplayground.domain.auth.dto.UserPrincipal;
import com.apple.appleplayground.domain.image.dto.response.ImageResponse;
import com.apple.appleplayground.domain.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 이미지 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Tag(name = "Image API", description = "AWS S3 이미지 업로드 및 관리 API")
public class ImageController {
    
    private final ImageService imageService;
    
    @Operation(summary = "이미지 업로드", description = "AWS S3에 이미지를 업로드합니다. 파일 크기에 따라 자동으로 동기/비동기 처리됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "이미지 업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 크기"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "500", description = "업로드 실패")
    })
    @PostMapping("/upload")
    public CompletableFuture<ResponseEntity<ImageResponse>> uploadImage(
            @Parameter(description = "업로드할 이미지 파일") @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        return imageService.uploadImage(currentUser.getId(), file)
                .thenApply(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .exceptionally(throwable -> {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }
    
    @Operation(summary = "이미지 정보 조회", description = "지정된 ID의 이미지 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이미지 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 이미지")
    })
    @GetMapping("/{imageId}")
    public ResponseEntity<ImageResponse> getImage(
            @Parameter(description = "이미지 ID") @PathVariable Long imageId) {
        
        ImageResponse response = imageService.getImage(imageId);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "사용자 이미지 목록 조회", description = "지정된 사용자가 업로드한 이미지 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이미지 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ImageResponse>> getUserImages(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        List<ImageResponse> response = imageService.getUserImages(userId, pageable);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "이미지 삭제", description = "자신이 업로드한 이미지를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "이미지 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 이미지"),
        @ApiResponse(responseCode = "500", description = "삭제 실패")
    })
    @DeleteMapping("/{imageId}")
    public CompletableFuture<ResponseEntity<Void>> deleteImage(
            @Parameter(description = "이미지 ID") @PathVariable Long imageId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        return imageService.deleteImage(imageId, currentUser.getId())
                .thenApply(result -> ResponseEntity.noContent().<Void>build())
                .exceptionally(throwable -> {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Void>build();
                });
    }
}
