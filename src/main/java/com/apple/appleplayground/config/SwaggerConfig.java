package com.apple.appleplayground.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🍎 Apple Playground API")
                        .description("GitHub OAuth 기반 소셜 로그인 시스템 API 문서")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Apple Playground Team")
                                .email("support@appleplayground.com")
                                .url("https://github.com/your-username/appleplayground"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("개발 서버"),
                        new Server()
                                .url("https://appleplayground.com")
                                .description("운영 서버")))
                .components(new Components()
                        .addSecuritySchemes("OAuth2", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .description("GitHub OAuth2 인증")
                                .flows(new io.swagger.v3.oas.models.security.OAuthFlows()
                                        .authorizationCode(new io.swagger.v3.oas.models.security.OAuthFlow()
                                                .authorizationUrl("https://github.com/login/oauth/authorize")
                                                .tokenUrl("https://github.com/login/oauth/access_token")
                                                .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                                        .addString("user:email", "사용자 이메일 접근")
                                                        .addString("read:user", "사용자 정보 읽기")))))
                        .addSecuritySchemes("SessionAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("JSESSIONID")
                                .description("세션 기반 인증")))
                .addSecurityItem(new SecurityRequirement().addList("SessionAuth"))
                .addSecurityItem(new SecurityRequirement().addList("OAuth2"));
    }
}
