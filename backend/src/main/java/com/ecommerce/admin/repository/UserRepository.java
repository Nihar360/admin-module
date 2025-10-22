package com.ecommerce.admin.repository;

import com.ecommerce.admin.model.User;
import com.ecommerce.admin.model.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    List<User> findByRole(UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' AND " +
           "(:search IS NULL OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.mobile) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:isActive IS NULL OR u.isActive = :isActive)")
    Page<User> findCustomers(
        @Param("search") String search,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );
    
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    Long countByRole(UserRole role);
}
