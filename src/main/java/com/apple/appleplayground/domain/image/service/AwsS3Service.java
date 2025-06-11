package com.apple.appleplayground.domain.image.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * AWS S3 서비스 (비동기 업로드 지원)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AwsS3Service {
    
    private final S3Client s3Client;
    private final S3AsyncClient s3AsyncClient;
    
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    @Value("${aws.s3.region}")
    private String region;
    
    /**
     * 비동기 파일 업로드
     */
    @Async("fileUploadExecutor")
    public CompletableFuture<String> uploadFileAsync(MultipartFile file, String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Starting async upload for file: {}", fileName);
                
                // S3에 업로드할 요청 생성
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType(file.getContentType())
                        .contentLength(file.getSize())
                        .serverSideEncryption(ServerSideEncryption.AES256) // 암호화
                        .storageClass(StorageClass.STANDARD_IA) // 비용 최적화
                        .build();
                
                // 비동기 업로드 실행
                s3AsyncClient.putObject(putObjectRequest, AsyncRequestBody.fromBytes(file.getBytes()))
                        .join(); // 업로드 완료까지 대기
                
                String fileUrl = generatePublicUrl(fileName);
                log.info("Async upload completed for file: {} -> {}", fileName, fileUrl);
                
                return fileUrl;
                
            } catch (IOException e) {
                log.error("Failed to upload file {} to S3: {}", fileName, e.getMessage());
                throw new RuntimeException("파일 업로드에 실패했습니다.", e);
            }
        });
    }
    
    /**
     * 동기 파일 업로드 (작은 파일용)
     */
    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        log.info("Starting sync upload for file: {}", fileName);
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .serverSideEncryption(ServerSideEncryption.AES256)
                .storageClass(StorageClass.STANDARD_IA)
                .build();
        
        s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                file.getInputStream(), file.getSize()));
        
        String fileUrl = generatePublicUrl(fileName);
        log.info("Sync upload completed for file: {} -> {}", fileName, fileUrl);
        
        return fileUrl;
    }
    
    /**
     * 파일 삭제
     */
    @Async("fileUploadExecutor")
    public CompletableFuture<Void> deleteFileAsync(String fileName) {
        return CompletableFuture.runAsync(() -> {
            try {
                log.info("Starting delete for file: {}", fileName);
                
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build();
                
                s3Client.deleteObject(deleteObjectRequest);
                log.info("File deleted successfully: {}", fileName);
                
            } catch (Exception e) {
                log.error("Failed to delete file {} from S3: {}", fileName, e.getMessage());
                throw new RuntimeException("파일 삭제에 실패했습니다.", e);
            }
        });
    }
    
    /**
     * Pre-signed URL 생성 (임시 업로드 URL)
     */
    public String generatePresignedUploadUrl(String fileName, Duration expiration) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        
        S3Presigner presigner = S3Presigner.create();
        
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .putObjectRequest(putObjectRequest)
                .build();
        
        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        
        String presignedUrl = presignedRequest.url().toString();
        log.info("Generated presigned upload URL for file: {} (expires in {})", fileName, expiration);
        
        presigner.close();
        return presignedUrl;
    }
    
    /**
     * Pre-signed URL 생성 (임시 다운로드 URL)
     */
    public String generatePresignedDownloadUrl(String fileName, Duration expiration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        
        S3Presigner presigner = S3Presigner.create();
        
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(getObjectRequest)
                .build();
        
        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
        
        String presignedUrl = presignedRequest.url().toString();
        presigner.close();
        return presignedUrl;
    }
    
    /**
     * 파일 존재 여부 확인
     */
    public boolean doesFileExist(String fileName) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            
            s3Client.headObject(headObjectRequest);
            return true;
            
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("Error checking file existence: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 공개 URL 생성
     */
    private String generatePublicUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }
    
    /**
     * 멀티파트 업로드 시작 (대용량 파일용)
     */
    public String initiateMultipartUpload(String fileName, String contentType) {
        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .serverSideEncryption(ServerSideEncryption.AES256)
                .build();
        
        CreateMultipartUploadResponse response = s3Client.createMultipartUpload(createRequest);
        log.info("Initiated multipart upload for file: {} with uploadId: {}", fileName, response.uploadId());
        
        return response.uploadId();
    }
    
    /**
     * 버킷 정책 확인 (개발용)
     */
    public void validateBucketAccess() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            log.info("S3 bucket access validated successfully: {}", bucketName);
        } catch (Exception e) {
            log.error("Failed to access S3 bucket {}: {}", bucketName, e.getMessage());
            throw new RuntimeException("S3 버킷에 접근할 수 없습니다.", e);
        }
    }
}
