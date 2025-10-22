package com.ecommerce.admin.repository;

import com.ecommerce.admin.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByUserId(Long userId);
    
    List<Address> findByUserIdAndIsDefault(Long userId, Boolean isDefault);
}
