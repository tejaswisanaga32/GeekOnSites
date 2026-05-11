package com.geekonsite.repository;

import com.geekonsite.model.CallSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CallSessionRepository extends MongoRepository<CallSession, String> {
    
    Optional<CallSession> findBySessionId(String sessionId);
    
    List<CallSession> findByPhoneNumber(String phoneNumber);
    
    List<CallSession> findByStatus(CallSession.CallStatus status);
    
    List<CallSession> findByCurrentLevel(CallSession.SupportLevel level);
    
    List<CallSession> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    List<CallSession> findByResolvedFalseAndStatusNot(CallSession.CallStatus status);
    
    long countByStatusAndStartTimeAfter(CallSession.CallStatus status, LocalDateTime after);
}
