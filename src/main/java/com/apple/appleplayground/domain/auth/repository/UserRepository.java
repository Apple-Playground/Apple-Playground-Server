package com.apple.appleplayground.domain.auth.repository;

import com.apple.appleplayground.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByGithubId(String githubId);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByGithubId(String githubId);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
