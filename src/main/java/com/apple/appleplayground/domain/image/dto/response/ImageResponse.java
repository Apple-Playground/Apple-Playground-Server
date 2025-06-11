package com.apple.appleplayground.domain.image.dto.response;

import com.apple.appleplayground.domain.image.entity.Image;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 이미지 응답 DTO
 */
@Getter
@Builder
@Schema(description = "이미지 응답")
public class ImageResponse {
    
    @Schema(description = "이미지 ID", example = "1")
    private Long id;
    
    @Schema(description = "파일명", example = "profile_image.jpg")
    private String fileName;
    
    @Schema(description = "파일 URL", example = "https://r2.dev/bucket/profile_image.jpg")
    private String fileUrl;
    
    @Schema(description = "파일 크기 (bytes)", example = "1024000")
    private Long fileSize;
    
    @Schema(description = "MIME 타입", example = "image/jpeg")
    private String mimeType;
    
    @Schema(description = "업로드 일시", example = "2024-01-01T10:30:00")
    private LocalDateTime createdAt;
    
    /**
     * Image 엔티티로부터 ImageResponse 생성
     */
    public static ImageResponse from(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .fileName(image.getFileName())
                .fileUrl(image.getFileUrl())
                .fileSize(image.getFileSize())
                .mimeType(image.getMimeType())
                .createdAt(image.getCreatedAt())
                .build();
    }
}
