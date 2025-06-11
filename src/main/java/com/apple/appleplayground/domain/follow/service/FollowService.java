package com.apple.appleplayground.domain.follow.service;

import com.apple.appleplayground.domain.auth.entity.User;
import com.apple.appleplayground.domain.auth.repository.UserRepository;
import com.apple.appleplayground.domain.follow.dto.response.FollowListResponse;
import com.apple.appleplayground.domain.follow.dto.response.FollowStatusResponse;
import com.apple.appleplayground.domain.follow.entity.Follow;
import com.apple.appleplayground.domain.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 팔로우 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FollowService {
    
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    
    /**
     * 사용자 팔로우
     */
    @Transactional
    public FollowStatusResponse followUser(Long currentUserId, Long targetUserId) {
        // 자기 자신을 팔로우하는 것을 방지
        if (currentUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }
        
        User currentUser = findUserById(currentUserId);
        User targetUser = findUserById(targetUserId);
        
        // 이미 팔로우한 경우 예외 처리
        if (followRepository.existsByFollowerAndFollowing(currentUser, targetUser)) {
            throw new IllegalStateException("이미 팔로우한 사용자입니다.");
        }
        
        // 팔로우 관계 생성
        Follow follow = Follow.create(currentUser, targetUser);
        followRepository.save(follow);
        
        // 캐시 카운트 업데이트
        currentUser.incrementFollowingCount();
        targetUser.incrementFollowersCount();
        
        userRepository.save(currentUser);
        userRepository.save(targetUser);
        
        log.info("User {} followed user {}", currentUserId, targetUserId);
        
        return FollowStatusResponse.of(
            true, 
            targetUser.getFollowersCount(), 
            currentUser.getFollowingCount()
        );
    }
    
    /**
     * 사용자 언팔로우
     */
    @Transactional
    public FollowStatusResponse unfollowUser(Long currentUserId, Long targetUserId) {
        User currentUser = findUserById(currentUserId);
        User targetUser = findUserById(targetUserId);
        
        // 팔로우 관계가 없는 경우 예외 처리
        if (!followRepository.existsByFollowerAndFollowing(currentUser, targetUser)) {
            throw new IllegalStateException("팔로우하지 않은 사용자입니다.");
        }
        
        // 팔로우 관계 삭제
        followRepository.deleteByFollowerAndFollowing(currentUser, targetUser);
        
        // 캐시 카운트 업데이트
        currentUser.decrementFollowingCount();
        targetUser.decrementFollowersCount();
        
        userRepository.save(currentUser);
        userRepository.save(targetUser);
        
        log.info("User {} unfollowed user {}", currentUserId, targetUserId);
        
        return FollowStatusResponse.of(
            false, 
            targetUser.getFollowersCount(), 
            currentUser.getFollowingCount()
        );
    }
    
    /**
     * 팔로우 상태 확인
     */
    public FollowStatusResponse getFollowStatus(Long currentUserId, Long targetUserId) {
        User currentUser = findUserById(currentUserId);
        User targetUser = findUserById(targetUserId);
        
        boolean isFollowing = followRepository.existsByFollowerAndFollowing(currentUser, targetUser);
        
        return FollowStatusResponse.of(
            isFollowing, 
            targetUser.getFollowersCount(), 
            targetUser.getFollowingCount()
        );
    }
    
    /**
     * 팔로워 목록 조회
     */
    public FollowListResponse getFollowers(Long userId, Pageable pageable) {
        User user = findUserById(userId);
        Page<User> followers = followRepository.findFollowersByUser(user, pageable);
        return FollowListResponse.from(followers);
    }
    
    /**
     * 팔로잉 목록 조회
     */
    public FollowListResponse getFollowing(Long userId, Pageable pageable) {
        User user = findUserById(userId);
        Page<User> following = followRepository.findFollowingByUser(user, pageable);
        return FollowListResponse.from(following);
    }
    
    /**
     * 사용자 ID로 User 엔티티 조회
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));
    }
}
