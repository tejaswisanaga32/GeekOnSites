package com.geekonsite.repository;

import com.geekonsite.model.Agent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository extends MongoRepository<Agent, String> {
    
    Optional<Agent> findByAgentId(String agentId);
    
    Optional<Agent> findByEmail(String email);
    
    Optional<Agent> findByAgentIdOrEmail(String agentId, String email);
    
    List<Agent> findByActiveTrue();
    
    boolean existsByAgentId(String agentId);
    
    boolean existsByEmail(String email);
}
