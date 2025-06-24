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
                        .maxConnections(200)
                        .connectionMaxIdleTime(Duration.ofMinutes(5))
                        .connectionTimeout(Duration.ofSeconds(30))
                        .socketTimeout(Duration.ofMinutes(2))
                        .tcpKeepAlive(true)
                        .useIdleConnectionReaper(true)
                        .expectContinueEnabled(true)
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
                        .maxConcurrency(200)
                        .maxPendingConnectionAcquires(10000)
                        .connectionTimeout(Duration.ofSeconds(30))
                        .connectionAcquisitionTimeout(Duration.ofSeconds(60))
                        .connectionTimeToLive(Duration.ofMinutes(10))
                        .connectionMaxIdleTime(Duration.ofMinutes(5))
                        .useIdleConnectionReaper(true)
                        .tcpKeepAlive(true)
                        .http2Configuration(builder -> builder
                                .maxStreams(100L)
                                .initialWindowSize(1048576)
                                .healthCheckPingPeriod(Duration.ofSeconds(30))
                        )
                        .build())
                .build();
    }
}