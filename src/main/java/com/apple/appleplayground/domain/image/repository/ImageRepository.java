package com.apple.appleplayground.domain.image.repository;

import com.apple.appleplayground.domain.auth.entity.User;
import com.apple.appleplayground.domain.image.entity.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 이미지 Repository
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    
    /**
     * 특정 사용자가 업로드한 이미지 조회 (최신순)
     */
    Page<Image> findByUploadedByOrderByCreatedAtDesc(User uploadedBy, Pageable pageable);
    
    /**
     * 파일명으로 이미지 조회
     */
    Image findByFileName(String fileName);
    
    /**
     * 특정 사용자의 이미지 개수
     */
    long countByUploadedBy(User uploadedBy);
}
