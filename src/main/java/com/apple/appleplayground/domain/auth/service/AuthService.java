package com.apple.appleplayground.domain.auth.service;

import com.apple.appleplayground.domain.auth.dto.UserPrincipal;
import com.apple.appleplayground.domain.auth.dto.response.AuthMeResponse;
import com.apple.appleplayground.domain.auth.dto.response.AuthStatusResponse;
import com.apple.appleplayground.domain.auth.dto.response.LogoutResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final Validator validator;
    
    /**
     * 현재 인증된 사용자 정보를 반환
     */
    public AuthMeResponse getCurrentUserInfo(Authentication authentication) {
        if (!isAuthenticated(authentication)) {
        AuthMeResponse response = AuthMeResponse.builder()
                .authenticated(false)
                .build();
                
        validateResponse(response);
        return response;
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        AuthMeResponse.UserInfo userInfo = AuthMeResponse.UserInfo.builder()
                .id(userPrincipal.getId())
                .name(userPrincipal.getName())
                .email(userPrincipal.getEmail())
                .username(userPrincipal.getUsername())
                .avatarUrl(userPrincipal.getAvatarUrl())
                .build();
        
        AuthMeResponse response = AuthMeResponse.builder()
                .authenticated(true)
                .user(userInfo)
                .build();
                
        validateResponse(response);
        return response;
    }
    
    /**
     * 인증 상태 확인
     */
    public AuthStatusResponse getAuthenticationStatus(Authentication authentication) {
        boolean authenticated = isAuthenticated(authentication);
        
        AuthStatusResponse response = AuthStatusResponse.builder()
                .authenticated(authenticated)
                .timestamp(System.currentTimeMillis())
                .build();
                
        validateResponse(response);
        return response;
    }
    
    /**
     * 로그아웃 처리
     */
    public LogoutResponse processLogout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
                log.info("Session invalidated successfully");
            }
            
            LogoutResponse response = LogoutResponse.builder()
                    .success(true)
                    .message("로그아웃이 완료되었습니다.")
                    .build();
                    
            validateResponse(response);
            return response;
                    
        } catch (Exception e) {
            log.error("Logout process failed", e);
            LogoutResponse response = LogoutResponse.builder()
                    .success(false)
                    .message("로그아웃 처리 중 오류가 발생했습니다.")
                    .build();
                    
            validateResponse(response);
            return response;
        }
    }
    
    /**
     * 인증 여부 확인 헬퍼 메서드
     */
    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null 
                && authentication.isAuthenticated() 
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
    
    /**
     * 응답 DTO 검증 헬퍼 메서드
     */
    private <T> void validateResponse(T response) {
        Set<ConstraintViolation<T>> violations = validator.validate(response);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            log.error("Response validation failed: {}", sb.toString());
            throw new IllegalStateException("Response validation failed: " + sb.toString());
        }
    }
}
