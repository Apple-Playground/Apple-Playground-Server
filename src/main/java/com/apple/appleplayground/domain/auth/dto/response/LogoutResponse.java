package com.apple.appleplayground.domain.auth.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutResponse {
    
    @NotNull
    private Boolean success;
    
    @NotBlank
    private String message;
}
