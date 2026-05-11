package com.geekonsite.repository;

import com.geekonsite.model.Call;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CallRepository extends MongoRepository<Call, String> {
    
    Optional<Call> findByCallId(String callId);
    
    List<Call> findByAgentId(String agentId);
    
    List<Call> findByCustomerId(String customerId);
    
    List<Call> findByStatus(String status);
    
    List<Call> findByAgentIdAndStatus(String agentId, String status);
    
    List<Call> findByAgentIdAndStatusNot(String agentId, String status);
    
    long countByStatus(String status);
    
    long countByAgentIdAndStatus(String agentId, String status);
    
    List<Call> findByStatusOrderByCreatedAtAsc(String status);
    
    boolean existsByCallId(String callId);
}
