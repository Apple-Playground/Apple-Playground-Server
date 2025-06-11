package com.apple.appleplayground.domain.follow.repository;

import com.apple.appleplayground.domain.auth.entity.User;
import com.apple.appleplayground.domain.follow.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 팔로우 관계 Repository
 */
@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    
    /**
     * 팔로우 관계 존재 확인
     */
    boolean existsByFollowerAndFollowing(User follower, User following);
    
    /**
     * 팔로우 관계 조회
     */
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    
    /**
     * 특정 사용자의 팔로워 목록 조회 (페이징)
     */
    @Query("SELECT f.follower FROM Follow f WHERE f.following = :user")
    Page<User> findFollowersByUser(@Param("user") User user, Pageable pageable);
    
    /**
     * 특정 사용자의 팔로잉 목록 조회 (페이징)
     */
    @Query("SELECT f.following FROM Follow f WHERE f.follower = :user")
    Page<User> findFollowingByUser(@Param("user") User user, Pageable pageable);
    
    /**
     * 팔로워 수 조회
     */
    long countByFollowing(User following);
    
    /**
     * 팔로잉 수 조회
     */
    long countByFollower(User follower);
    
    /**
     * 팔로우 관계 삭제
     */
    void deleteByFollowerAndFollowing(User follower, User following);
}
