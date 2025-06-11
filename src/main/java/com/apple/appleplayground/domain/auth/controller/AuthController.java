package com.apple.appleplayground.domain.auth.controller;

import com.apple.appleplayground.domain.auth.dto.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() 
            && !(authentication instanceof AnonymousAuthenticationToken)) {
            
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "user", Map.of(
                    "id", userPrincipal.getId(),
                    "name", userPrincipal.getName(),
                    "email", userPrincipal.getEmail()
                )
            ));
        }
        
        return ResponseEntity.ok(Map.of("authenticated", false));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "로그아웃 완료"
        ));
    }
    
    @GetMapping("/status")
    public ResponseEntity<?> getAuthStatus(Authentication authentication) {
        boolean isAuthenticated = authentication != null 
            && authentication.isAuthenticated() 
            && !(authentication instanceof AnonymousAuthenticationToken);
            
        return ResponseEntity.ok(Map.of(
            "authenticated", isAuthenticated,
            "timestamp", System.currentTimeMillis()
        ));
    }
}
