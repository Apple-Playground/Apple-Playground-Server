package com.apple.appleplayground.global.external;

import com.apple.appleplayground.domain.auth.dto.GitHubUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "github-api", url = "https://api.github.com")
public interface GitHubApiClient {
    
    @GetMapping("/user")
    GitHubUserDto getUser(@RequestHeader("Authorization") String token);
    
    @GetMapping("/user/emails")
    GitHubUserDto.Email[] getUserEmails(@RequestHeader("Authorization") String token);
}
