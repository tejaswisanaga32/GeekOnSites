package com.geekonsite.controller;

import com.geekonsite.dto.ApiResponse;
import com.geekonsite.model.SupportTicket;
import com.geekonsite.repository.SupportTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicTicketController {
    
    @Autowired
    private SupportTicketRepository supportTicketRepository;
    
    /**
     * Get all tickets for agents (public endpoint)
     * GET /api/public/tickets
     */
    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse> getPublicTickets() {
        try {
            List<SupportTicket> tickets = supportTicketRepository.findAll();
            return ResponseEntity.ok(ApiResponse.success("Public tickets retrieved", tickets));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to retrieve tickets: " + e.getMessage()));
        }
    }
    
    /**
     * Update ticket status for agents (public endpoint)
     * PUT /api/public/tickets/{ticketNumber}/status
     */
    @PutMapping("/tickets/{ticketNumber}/status")
    public ResponseEntity<?> updateTicketStatusPublic(
            @PathVariable String ticketNumber,
            @RequestBody Map<String, Object> request) {
        
        java.util.Optional<SupportTicket> ticketOpt = supportTicketRepository.findByTicketNumber(ticketNumber);
        if (ticketOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("Ticket not found"));
        }
        
        SupportTicket ticket = ticketOpt.get();
        String status = (String) request.get("status");
        String notes = (String) request.get("notes");
        
        try {
            SupportTicket.TicketStatus newStatus = SupportTicket.TicketStatus.valueOf(status.toUpperCase());
            ticket.setStatus(newStatus);
            ticket.setUpdatedAt(LocalDateTime.now());
            
            if (notes != null && !notes.trim().isEmpty()) {
                ticket.addNote(notes, "agent", false);
            }
            
            supportTicketRepository.save(ticket);
            
            return ResponseEntity.ok(ApiResponse.success("Ticket status updated successfully", ticket));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid status"));
        }
    }
}
