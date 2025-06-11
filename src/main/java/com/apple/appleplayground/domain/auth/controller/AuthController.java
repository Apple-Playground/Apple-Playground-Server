package com.apple.appleplayground.domain.auth.controller;

import com.apple.appleplayground.domain.auth.dto.response.AuthMeResponse;
import com.apple.appleplayground.domain.auth.dto.response.AuthStatusResponse;
import com.apple.appleplayground.domain.auth.dto.response.LogoutResponse;
import com.apple.appleplayground.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "인증 관리 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    
    @Operation(summary = "현재 사용자 정보 조회", description = "인증된 사용자의 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/me")
    public ResponseEntity<AuthMeResponse> getCurrentUser(Authentication authentication) {
        AuthMeResponse response = authService.getCurrentUserInfo(authentication);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "로그아웃", description = "현재 세션을 무효화하고 로그아웃을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpServletRequest request) {
        LogoutResponse response = authService.processLogout(request);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "인증 상태 확인", description = "현재 사용자의 인증 상태를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상태 확인 성공")
    })
    @GetMapping("/status")
    public ResponseEntity<AuthStatusResponse> getAuthStatus(Authentication authentication) {
        AuthStatusResponse response = authService.getAuthenticationStatus(authentication);
        return ResponseEntity.ok(response);
    }
}
