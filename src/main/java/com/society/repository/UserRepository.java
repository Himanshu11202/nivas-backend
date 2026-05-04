package com.society.repository;

import com.society.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByFlatNumber(String flatNumber);
    List<User> findByRole(User.Role role);
    List<User> findByStatus(User.UserStatus status);
    List<User> findByRoleAndStatus(User.Role role, User.UserStatus status);
    List<User> findByFlatNumber(String flatNumber);
    List<User> findBySocietyId(Long societyId);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(User.Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    long countByStatus(User.UserStatus status);
}
