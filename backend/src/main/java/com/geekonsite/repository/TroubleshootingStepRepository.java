package com.geekonsite.repository;

import com.geekonsite.model.TroubleshootingStep;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TroubleshootingStepRepository extends MongoRepository<TroubleshootingStep, String> {
    
    List<TroubleshootingStep> findByFlowIdOrderByStepIdAsc(String flowId);
    
    Optional<TroubleshootingStep> findByFlowIdAndStepId(String flowId, String stepId);
    
    List<TroubleshootingStep> findByRequiresTechnicianFalse();
    
    List<TroubleshootingStep> findByIsFinalStepTrue();
}
