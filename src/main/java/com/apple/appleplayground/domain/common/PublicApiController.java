package com.apple.appleplayground.domain.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@Tag(name = "공개 API", description = "인증이 필요하지 않은 공개 API")
public class PublicApiController {

    @GetMapping("/health")
    @Operation(
        summary = "헬스 체크",
        description = "애플리케이션의 상태를 확인합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "서비스 정상 동작",
            content = @Content(schema = @Schema(implementation = HealthResponse.class))
        )
    })
    public ResponseEntity<HealthResponse> healthCheck() {
        return ResponseEntity.ok(new HealthResponse(
            "UP",
            "Apple Playground API is running",
            LocalDateTime.now()
        ));
    }

    @GetMapping("/info")
    @Operation(
        summary = "애플리케이션 정보",
        description = "애플리케이션의 기본 정보를 제공합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "애플리케이션 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = AppInfo.class))
        )
    })
    public ResponseEntity<AppInfo> getAppInfo() {
        return ResponseEntity.ok(new AppInfo(
            "Apple Playground",
            "v1.0.0",
            "GitHub OAuth 기반 소셜 로그인 시스템",
            Map.of(
                "swagger-ui", "/swagger-ui.html",
                "api-docs", "/api-docs",
                "github", "https://github.com/your-username/appleplayground"
            )
        ));
    }

    @Schema(description = "헬스 체크 응답")
    public record HealthResponse(
            @Schema(description = "서비스 상태", example = "UP")
            String status,
            @Schema(description = "상태 메시지", example = "Apple Playground API is running")
            String message,
            @Schema(description = "응답 시간")
            LocalDateTime timestamp
    ) {}

    @Schema(description = "애플리케이션 정보")
    public record AppInfo(
            @Schema(description = "애플리케이션 이름", example = "Apple Playground")
            String name,
            @Schema(description = "버전", example = "v1.0.0")
            String version,
            @Schema(description = "설명", example = "GitHub OAuth 기반 소셜 로그인 시스템")
            String description,
            @Schema(description = "관련 링크")
            Map<String, String> links
    ) {}
}
