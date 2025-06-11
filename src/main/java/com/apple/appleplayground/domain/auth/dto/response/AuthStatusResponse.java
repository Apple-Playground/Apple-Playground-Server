package com.apple.appleplayground.domain.auth.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthStatusResponse {
    
    @NotNull
    private Boolean authenticated;
    
    @NotNull
    private Long timestamp;
}
