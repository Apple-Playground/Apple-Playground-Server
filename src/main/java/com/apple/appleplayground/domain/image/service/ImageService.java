package com.apple.appleplayground.domain.image.service;

import com.apple.appleplayground.domain.auth.entity.User;
import com.apple.appleplayground.domain.auth.repository.UserRepository;
import com.apple.appleplayground.domain.image.dto.response.ImageResponse;
import com.apple.appleplayground.domain.image.entity.Image;
import com.apple.appleplayground.domain.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 이미지 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ImageService {
    
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final AwsS3Service awsS3Service;
    
    // 파일 크기 임계값 (5MB) - 이보다 크면 비동기 업로드
    private static final long ASYNC_UPLOAD_THRESHOLD = 5 * 1024 * 1024;
    
    /**
     * 이미지 업로드 (자동으로 동기/비동기 선택)
     */
    @Transactional
    public CompletableFuture<ImageResponse> uploadImage(Long userId, MultipartFile file) {
        User user = findUserById(userId);
        
        // 파일 검증
        validateImageFile(file);
        
        // 고유한 파일명 생성
        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String uniqueFileName = generateUniqueFileName(userId, fileExtension);
        
        // 파일 크기에 따라 동기/비동기 업로드 결정
        if (file.getSize() > ASYNC_UPLOAD_THRESHOLD) {
            return uploadImageAsync(user, file, uniqueFileName);
        } else {
            return uploadImageSync(user, file, uniqueFileName);
        }
    }
    
    /**
     * 비동기 이미지 업로드
     */
    @Async("fileUploadExecutor")
    @Transactional
    public CompletableFuture<ImageResponse> uploadImageAsync(User user, MultipartFile file, String fileName) {
        return awsS3Service.uploadFileAsync(file, fileName)
                .thenApply(fileUrl -> {
                    // 비동기 컨텍스트에서 DB 저장
                    Image image = Image.create(
                            fileName,
                            fileUrl,
                            file.getSize(),
                            file.getContentType(),
                            user
                    );
                    
                    Image savedImage = imageRepository.save(image);
                    log.info("Async image upload completed: {} by user {}", savedImage.getId(), user.getId());
                    
                    return ImageResponse.from(savedImage);
                })
                .exceptionally(throwable -> {
                    log.error("Async image upload failed for user {}: {}", user.getId(), throwable.getMessage());
                    throw new RuntimeException("이미지 업로드에 실패했습니다.", throwable);
                });
    }
    
    /**
     * 동기 이미지 업로드
     */
    @Transactional
    public CompletableFuture<ImageResponse> uploadImageSync(User user, MultipartFile file, String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 동기 업로드
                String fileUrl = awsS3Service.uploadFile(file, fileName);
                
                // DB 저장
                Image image = Image.create(
                        fileName,
                        fileUrl,
                        file.getSize(),
                        file.getContentType(),
                        user
                );
                
                Image savedImage = imageRepository.save(image);
                log.info("Sync image upload completed: {} by user {}", savedImage.getId(), user.getId());
                
                return ImageResponse.from(savedImage);
                
            } catch (Exception e) {
                log.error("Sync image upload failed for user {}: {}", user.getId(), e.getMessage());
                throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
            }
        });
    }
    
    /**
     * Pre-signed URL 생성 (클라이언트 직접 업로드용)
     */
    @Transactional
    public String generateUploadUrl(Long userId, String originalFileName, String contentType) {
        User user = findUserById(userId);
        
        // 파일 확장자 검증
        if (!isValidImageContentType(contentType)) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다.");
        }
        
        String fileExtension = getFileExtensionFromContentType(contentType);
        String uniqueFileName = generateUniqueFileName(userId, fileExtension);
        
        // 15분 후 만료되는 업로드 URL 생성
        String presignedUrl = awsS3Service.generatePresignedUploadUrl(uniqueFileName, Duration.ofMinutes(15));
        
        // 메타데이터만 미리 저장 (업로드 완료 후 업데이트)
        Image image = Image.create(
                uniqueFileName,
                "", // URL은 업로드 완료 후 업데이트
                0L, // 크기는 업로드 완료 후 업데이트
                contentType,
                user
        );
        
        imageRepository.save(image);
        log.info("Generated presigned upload URL for user {}: {}", userId, uniqueFileName);
        
        return presignedUrl;
    }
    
    /**
     * 이미지 정보 조회
     */
    public ImageResponse getImage(Long imageId) {
        Image image = findImageById(imageId);
        return ImageResponse.from(image);
    }
    
    /**
     * 사용자의 이미지 목록 조회
     */
    public List<ImageResponse> getUserImages(Long userId, Pageable pageable) {
        User user = findUserById(userId);
        Page<Image> images = imageRepository.findByUploadedByOrderByCreatedAtDesc(user, pageable);
        
        return images.getContent().stream()
                .map(ImageResponse::from)
                .toList();
    }
    
    /**
     * 이미지 삭제
     */
    @Transactional
    public CompletableFuture<Void> deleteImage(Long imageId, Long currentUserId) {
        Image image = findImageById(imageId);
        
        // 업로드 권한 확인
        if (!image.isUploadedBy(findUserById(currentUserId))) {
            throw new IllegalArgumentException("이미지 삭제 권한이 없습니다.");
        }
        
        // 비동기로 S3에서 삭제
        return awsS3Service.deleteFileAsync(image.getFileName())
                .thenRun(() -> {
                    // DB에서 삭제
                    imageRepository.delete(image);
                    log.info("Image deleted: {} by user {}", imageId, currentUserId);
                })
                .exceptionally(throwable -> {
                    log.error("Failed to delete image {}: {}", imageId, throwable.getMessage());
                    throw new RuntimeException("이미지 삭제에 실패했습니다.", throwable);
                });
    }
    
    /**
     * 임시 다운로드 URL 생성
     */
    public String generateDownloadUrl(Long imageId, Duration expiration) {
        Image image = findImageById(imageId);
        return awsS3Service.generatePresignedDownloadUrl(image.getFileName(), expiration);
    }
    
    /**
     * 이미지 파일 검증
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        
        String contentType = file.getContentType();
        if (!isValidImageContentType(contentType)) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
        
        // 파일 크기 제한 (50MB)
        long maxSize = 50 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 크기는 50MB를 초과할 수 없습니다.");
        }
    }
    
    /**
     * 유효한 이미지 컨텐츠 타입 확인
     */
    private boolean isValidImageContentType(String contentType) {
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp")
        );
    }
    
    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }
    
    /**
     * 컨텐츠 타입에서 파일 확장자 추출
     */
    private String getFileExtensionFromContentType(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }
    
    /**
     * 고유한 파일명 생성
     */
    private String generateUniqueFileName(Long userId, String extension) {
        return String.format("user_%d/%s%s", userId, UUID.randomUUID().toString(), extension);
    }
    
    /**
     * 이미지 ID로 Image 엔티티 조회
     */
    private Image findImageById(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지입니다. ID: " + imageId));
    }
    
    /**
     * 사용자 ID로 User 엔티티 조회
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));
    }
}
