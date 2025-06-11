package com.apple.appleplayground.domain.blog.service;

import com.apple.appleplayground.domain.auth.entity.User;
import com.apple.appleplayground.domain.auth.repository.UserRepository;
import com.apple.appleplayground.domain.blog.dto.request.CreateBlogPostRequest;
import com.apple.appleplayground.domain.blog.dto.request.UpdateBlogPostRequest;
import com.apple.appleplayground.domain.blog.dto.response.BlogPostListResponse;
import com.apple.appleplayground.domain.blog.dto.response.BlogPostResponse;
import com.apple.appleplayground.domain.blog.entity.BlogPost;
import com.apple.appleplayground.domain.blog.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 블로그 포스트 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BlogPostService {
    
    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;
    
    /**
     * 블로그 포스트 생성
     */
    @Transactional
    public BlogPostResponse createPost(Long authorId, CreateBlogPostRequest request) {
        User author = findUserById(authorId);
        
        BlogPost blogPost = BlogPost.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .build();
        
        BlogPost savedPost = blogPostRepository.save(blogPost);
        log.info("Blog post created: {} by user {}", savedPost.getId(), authorId);
        
        return BlogPostResponse.from(savedPost);
    }
    
    /**
     * 블로그 포스트 상세 조회 (조회수 증가)
     */
    @Transactional
    public BlogPostResponse getPost(Long postId) {
        BlogPost blogPost = findBlogPostById(postId);
        
        // 조회수 증가 (원자적 연산)
        blogPostRepository.incrementViewCount(postId);
        blogPost.incrementViewCount(); // 메모리상 객체도 동기화
        
        return BlogPostResponse.from(blogPost);
    }
    
    /**
     * 전체 블로그 포스트 목록 조회
     */
    public BlogPostListResponse getAllPosts(Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.findAllByOrderByCreatedAtDesc(pageable);
        return BlogPostListResponse.from(posts);
    }
    
    /**
     * 특정 사용자의 블로그 포스트 목록 조회
     */
    public BlogPostListResponse getUserPosts(Long userId, Pageable pageable) {
        User user = findUserById(userId);
        Page<BlogPost> posts = blogPostRepository.findByAuthorOrderByCreatedAtDesc(user, pageable);
        return BlogPostListResponse.from(posts);
    }
    
    /**
     * 블로그 포스트 검색
     */
    public BlogPostListResponse searchPosts(String keyword, Pageable pageable) {
        Page<BlogPost> posts = blogPostRepository.findByTitleOrContentContainingIgnoreCase(keyword, pageable);
        return BlogPostListResponse.from(posts);
    }
    
    /**
     * 블로그 포스트 수정
     */
    @Transactional
    public BlogPostResponse updatePost(Long postId, Long currentUserId, UpdateBlogPostRequest request) {
        BlogPost blogPost = findBlogPostById(postId);
        
        // 작성자 권한 확인
        if (!blogPost.isAuthor(findUserById(currentUserId))) {
            throw new IllegalArgumentException("포스트 수정 권한이 없습니다.");
        }
        
        blogPost.updatePost(request.getTitle(), request.getContent());
        BlogPost updatedPost = blogPostRepository.save(blogPost);
        
        log.info("Blog post updated: {} by user {}", postId, currentUserId);
        
        return BlogPostResponse.from(updatedPost);
    }
    
    /**
     * 블로그 포스트 삭제
     */
    @Transactional
    public void deletePost(Long postId, Long currentUserId) {
        BlogPost blogPost = findBlogPostById(postId);
        
        // 작성자 권한 확인
        if (!blogPost.isAuthor(findUserById(currentUserId))) {
            throw new IllegalArgumentException("포스트 삭제 권한이 없습니다.");
        }
        
        blogPostRepository.delete(blogPost);
        log.info("Blog post deleted: {} by user {}", postId, currentUserId);
    }
    
    /**
     * 블로그 포스트 좋아요 증가
     */
    @Transactional
    public BlogPostResponse incrementLike(Long postId) {
        BlogPost blogPost = findBlogPostById(postId);
        
        // 좋아요 증가 (원자적 연산)
        blogPostRepository.incrementLikeCount(postId);
        blogPost.incrementLikeCount(); // 메모리상 객체도 동기화
        
        log.info("Like incremented for post: {}", postId);
        
        return BlogPostResponse.from(blogPost);
    }
    
    /**
     * 블로그 포스트 좋아요 감소
     */
    @Transactional
    public BlogPostResponse decrementLike(Long postId) {
        BlogPost blogPost = findBlogPostById(postId);
        
        // 좋아요 감소 (원자적 연산)
        blogPostRepository.decrementLikeCount(postId);
        blogPost.decrementLikeCount(); // 메모리상 객체도 동기화
        
        log.info("Like decremented for post: {}", postId);
        
        return BlogPostResponse.from(blogPost);
    }
    
    /**
     * 블로그 포스트 ID로 BlogPost 엔티티 조회
     */
    private BlogPost findBlogPostById(Long postId) {
        return blogPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 포스트입니다. ID: " + postId));
    }
    
    /**
     * 사용자 ID로 User 엔티티 조회
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userId));
    }
}
