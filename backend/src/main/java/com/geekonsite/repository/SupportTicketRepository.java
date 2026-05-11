package com.geekonsite.repository;

import com.geekonsite.model.SupportTicket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends MongoRepository<SupportTicket, String> {
    
    Optional<SupportTicket> findByTicketNumber(String ticketNumber);
    
    List<SupportTicket> findByCallSessionId(String callSessionId);
    
    List<SupportTicket> findByCustomerPhone(String customerPhone);
    
    List<SupportTicket> findByStatus(SupportTicket.TicketStatus status);
    
    List<SupportTicket> findBySupportLevel(SupportTicket.SupportLevel level);
    
    List<SupportTicket> findByAssignedTo(String assignedTo);
    
    List<SupportTicket> findByStatusAndSupportLevel(SupportTicket.TicketStatus status, SupportTicket.SupportLevel level);
    
    List<SupportTicket> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<SupportTicket> findByResolvedAtL1True();
    
    List<SupportTicket> findByResolvedRemotelyTrue();
    
    long countByStatus(SupportTicket.TicketStatus status);
    
    long countBySupportLevelAndStatus(SupportTicket.SupportLevel level, SupportTicket.TicketStatus status);
}
