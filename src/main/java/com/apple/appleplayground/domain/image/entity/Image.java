package com.apple.appleplayground.domain.image.entity;

import com.apple.appleplayground.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 이미지 저장소 엔티티 (Cloudflare R2 URL 관리)
 */
@Entity
@Table(
    name = "images",
    indexes = {
        @Index(name = "idx_uploaded_by", columnList = "uploaded_by"),
        @Index(name = "idx_created_at", columnList = "created_at")
    }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Image {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false, length = 1000)
    private String fileUrl;
    
    @Column
    private Long fileSize;
    
    @Column(length = 100)
    private String mimeType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 이미지 정보 생성
     */
    public static Image create(String fileName, String fileUrl, Long fileSize, 
                              String mimeType, User uploadedBy) {
        return Image.builder()
                .fileName(fileName)
                .fileUrl(fileUrl)
                .fileSize(fileSize)
                .mimeType(mimeType)
                .uploadedBy(uploadedBy)
                .build();
    }
    
    /**
     * 업로더 확인
     */
    public boolean isUploadedBy(User user) {
        return this.uploadedBy != null && this.uploadedBy.getId().equals(user.getId());
    }
}
