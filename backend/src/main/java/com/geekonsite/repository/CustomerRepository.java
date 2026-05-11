package com.geekonsite.repository;

import com.geekonsite.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    
    Optional<Customer> findByCustomerId(String customerId);
    
    Optional<Customer> findByEmail(String email);
    
    List<Customer> findByVerifiedTrue();
    
    List<Customer> findByVerifiedFalse();
    
    List<Customer> findByVerificationStatus(String status);
    
    boolean existsByEmail(String email);
    
    boolean existsByCustomerId(String customerId);
    
    Optional<Customer> findByPhone(String phone);
}
