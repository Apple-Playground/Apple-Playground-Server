package com.apple.appleplayground.domain.blog.repository;

import com.apple.appleplayground.domain.auth.entity.User;
import com.apple.appleplayground.domain.blog.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 블로그 포스트 Repository
 */
@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    
    /**
     * 특정 사용자의 블로그 포스트 조회 (최신순)
     */
    Page<BlogPost> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);
    
    /**
     * 전체 블로그 포스트 조회 (최신순)
     */
    Page<BlogPost> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 제목으로 검색
     */
    Page<BlogPost> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title, Pageable pageable);
    
    /**
     * 내용으로 검색
     */
    Page<BlogPost> findByContentContainingIgnoreCaseOrderByCreatedAtDesc(String content, Pageable pageable);
    
    /**
     * 제목 또는 내용으로 검색
     */
    @Query("SELECT bp FROM BlogPost bp WHERE " +
           "LOWER(bp.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(bp.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY bp.createdAt DESC")
    Page<BlogPost> findByTitleOrContentContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 조회수 증가 (원자적 연산)
     */
    @Modifying
    @Query("UPDATE BlogPost bp SET bp.viewCount = bp.viewCount + 1 WHERE bp.id = :id")
    int incrementViewCount(@Param("id") Long id);
    
    /**
     * 좋아요 증가 (원자적 연산)
     */
    @Modifying
    @Query("UPDATE BlogPost bp SET bp.likeCount = bp.likeCount + 1 WHERE bp.id = :id")
    int incrementLikeCount(@Param("id") Long id);
    
    /**
     * 좋아요 감소 (원자적 연산)
     */
    @Modifying
    @Query("UPDATE BlogPost bp SET bp.likeCount = bp.likeCount - 1 WHERE bp.id = :id AND bp.likeCount > 0")
    int decrementLikeCount(@Param("id") Long id);
    
    /**
     * 특정 사용자의 포스트 개수
     */
    long countByAuthor(User author);
}
