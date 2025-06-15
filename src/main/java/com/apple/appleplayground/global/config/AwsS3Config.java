package com.apple.appleplayground.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Duration;

/**
 * AWS S3 설정
 */
@Configuration
public class AwsS3Config {
    
    @Value("${aws.s3.access-key}")
    private String accessKey;
    
    @Value("${aws.s3.secret-key}")
    private String secretKey;
    
    @Value("${aws.s3.region}")
    private String region;
    
    /**
     * 고성능 동기 S3 클라이언트 (Apache HTTP Client 사용)
     */
    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .httpClient(ApacheHttpClient.builder()
                        // 연결 풀 설정
                        .maxConnections(200)                    // 최대 연결 수
                        .connectionMaxIdleTime(Duration.ofMinutes(5))  // 유휴 연결 유지 시간
                        .connectionTimeout(Duration.ofSeconds(30))     // 연결 타임아웃
                        .socketTimeout(Duration.ofMinutes(2))          // 소켓 타임아웃
                        // TCP 설정
                        .tcpKeepAlive(true)                     // TCP Keep-Alive 활성화
                        .useIdleConnectionReaper(true)         // 유휴 연결 정리
                        // 요청 재시도 설정
                        .expectContinueEnabled(true)           // HTTP 100-Continue 지원
                        .build())
                .build();
    }
    
    /**
     * 고성능 비동기 S3 클라이언트 (Netty 사용)
     */
    @Bean
    public S3AsyncClient s3AsyncClient() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        
        return S3AsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .httpClient(NettyNioAsyncHttpClient.builder()
                        // 연결 풀 설정
                        .maxConcurrency(200)                    // 최대 동시 연결 수
                        .maxPendingConnectionAcquires(10000)    // 대기 중인 연결 요청 수
                        .connectionTimeout(Duration.ofSeconds(30))     // 연결 타임아웃
                        .connectionAcquisitionTimeout(Duration.ofSeconds(60)) // 연결 획득 타임아웃
                        .connectionTimeToLive(Duration.ofMinutes(10))         // 연결 생존 시간
                        .connectionMaxIdleTime(Duration.ofMinutes(5))         // 최대 유휴 시간
                        // TCP 설정
                        .useIdleConnectionReaper(true)         // 유휴 연결 정리
                        .tcpKeepAlive(true)                     // TCP Keep-Alive
                        // HTTP/2 설정 (성능 향상)
                        .http2Configuration(builder -> builder
                                .maxStreams(100L)                // HTTP/2 최대 스트림 수
                                .initialWindowSize(1048576)     // 초기 윈도우 크기 (1MB)
                                .healthCheckPingPeriod(Duration.ofSeconds(30)) // 헬스체크 주기
                        )
                        .build())
                .build();
    }
}
