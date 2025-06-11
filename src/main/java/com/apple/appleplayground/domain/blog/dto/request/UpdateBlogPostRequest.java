package com.apple.appleplayground.domain.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 블로그 포스트 수정 요청 DTO
 */
@Getter
@NoArgsConstructor
@Schema(description = "블로그 포스트 수정 요청")
public class UpdateBlogPostRequest {
    
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 500, message = "제목은 500자를 초과할 수 없습니다.")
    @Schema(description = "포스트 제목", example = "Spring Boot 튜토리얼 (수정됨)", required = true)
    private String title;
    
    @NotBlank(message = "내용은 필수입니다.")
    @Schema(description = "포스트 내용", example = "수정된 내용입니다...", required = true)
    private String content;
}
