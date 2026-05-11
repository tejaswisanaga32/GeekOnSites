package com.geekonsite.controller;

import com.geekonsite.dto.ApiResponse;
import com.geekonsite.model.IVROption;
import com.geekonsite.model.SupportTicket;
import com.geekonsite.repository.SupportTicketRepository;
import com.geekonsite.service.SupportCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {
    
    @Autowired
    private SupportCallService supportCallService;
    
    @Autowired
    private SupportTicketRepository supportTicketRepository;
    
    /**
     * 1️⃣ Create Ticket (Entry Point) - PUBLIC, simulates IVR call
     * POST /api/tickets
     */
    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody Map<String, Object> request) {
        String ticketNumber = generateTicketNumber();
        
        SupportTicket ticket = new SupportTicket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setCustomerName((String) request.get("customerName"));
        ticket.setCustomerPhone((String) request.get("customerPhone"));
        ticket.setCustomerEmail((String) request.get("customerEmail"));
        ticket.setIssueDescription((String) request.get("issueDescription"));
        ticket.setCategory(parseCategory((String) request.get("category")));
        ticket.setStatus(SupportTicket.TicketStatus.OPEN);
        ticket.setSupportLevel(SupportTicket.SupportLevel.L1_FIRST_LINE);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        
        if (request.containsKey("callSessionId")) {
            ticket.setCallSessionId((String) request.get("callSessionId"));
        }
        
        // Add initial note
        ticket.addNote("Ticket created via API", "SYSTEM", true);
        
        SupportTicket saved = supportTicketRepository.save(ticket);
        
        return ResponseEntity.ok(ApiResponse.success("Ticket created successfully", 
            Map.of("ticketNumber", ticketNumber, "ticket", saved)));
    }
    
    /**
     * 2️⃣ Get All Tickets
     * GET /api/tickets
     */
    @GetMapping
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<?> getAllTickets() {
        List<SupportTicket> tickets = supportTicketRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("All tickets retrieved", tickets));
    }
    
    /**
     * Simple test endpoint to verify public access works
     * GET /api/public/test
     */
    @GetMapping("/public/test")
    public ResponseEntity<ApiResponse> testPublicAccess() {
        return ResponseEntity.ok(ApiResponse.success("Public access works!"));
    }
    
        
    /**
     * 3️⃣ Get Ticket by Phone
     * GET /api/tickets/phone/{phone}
     */
    @GetMapping("/phone/{phone}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'CUSTOMER')")
    public ResponseEntity<?> getTicketsByPhone(@PathVariable String phone) {
        List<SupportTicket> tickets = supportTicketRepository.findByCustomerPhone(phone);
        if (tickets.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("No tickets found for this phone number"));
        }
        return ResponseEntity.ok(ApiResponse.success("Tickets found", tickets));
    }
    
    /**
     * Get Ticket by ID/Number
     * GET /api/tickets/{id}
     */
    @GetMapping("/{ticketNumber}")
    @PreAuthorize("hasAnyRole('AGENT', 'CUSTOMER')")
    public ResponseEntity<?> getTicketByNumber(@PathVariable String ticketNumber) {
        return ResponseEntity.ok(supportCallService.getTicketDetails(ticketNumber));
    }
    
    /**
     * 4️⃣ Update Status (VERY IMPORTANT)
     * PUT /api/tickets/{id}/status
     */
    @PutMapping("/{ticketNumber}/status")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<?> updateTicketStatus(
            @PathVariable String ticketNumber,
            @RequestBody Map<String, String> request) {
        try {
            SupportTicket.TicketStatus status = SupportTicket.TicketStatus.valueOf(request.get("status"));
            String notes = request.getOrDefault("notes", null);
            return ResponseEntity.ok(supportCallService.updateTicketStatus(ticketNumber, status, notes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid status. Valid values: OPEN, IN_PROGRESS, WAITING_CUSTOMER, RESOLVED, CLOSED, ESCALATED"));
        }
    }
    
    /**
     * 5️⃣ Resolve Ticket
     * PUT /api/tickets/{id}/resolve
     */
    @PutMapping("/{ticketNumber}/resolve")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'TECH')")
    public ResponseEntity<?> resolveTicket(
            @PathVariable String ticketNumber,
            @RequestBody Map<String, String> request) {
        
        String resolution = request.getOrDefault("resolution", "Issue resolved");
        String resolutionNotes = request.getOrDefault("notes", "");
        String resolvedBy = request.getOrDefault("resolvedBy", "AGENT");
        
        java.util.Optional<SupportTicket> ticketOpt = supportTicketRepository.findByTicketNumber(ticketNumber);
        if (ticketOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("Ticket not found"));
        }
        
        SupportTicket ticket = ticketOpt.get();
        
        // Update ticket
        ticket.setStatus(SupportTicket.TicketStatus.RESOLVED);
        ticket.setResolution(resolution);
        ticket.setResolvedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        
        // Determine resolution type
        if (ticket.getSupportLevel() == SupportTicket.SupportLevel.L1_FIRST_LINE) {
            ticket.setResolvedAtL1(true);
        }
        if (ticket.getSupportLevel() == SupportTicket.SupportLevel.REMOTE_TECHNICIAN) {
            ticket.setResolvedRemotely(true);
        }
        
        // Add resolution note
        ticket.addNote("Ticket resolved by " + resolvedBy + ". " + resolutionNotes, resolvedBy, true);
        
        supportTicketRepository.save(ticket);
        
        return ResponseEntity.ok(ApiResponse.success("Ticket resolved successfully", 
            Map.of(
                "ticketNumber", ticketNumber,
                "status", "RESOLVED",
                "resolvedAt", ticket.getResolvedAt(),
                "resolution", resolution
            )));
    }
    
    /**
     * Assign Ticket to Agent
     * POST /api/tickets/{id}/assign
     */
    @PostMapping("/{ticketNumber}/assign")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<?> assignTicket(
            @PathVariable String ticketNumber,
            @RequestBody Map<String, String> request) {
        
        String agentId = request.get("agentId");
        String agentName = request.get("agentName");
        return ResponseEntity.ok(supportCallService.assignTicket(ticketNumber, agentId, agentName));
    }
    
    /**
     * Add Note to Ticket
     * POST /api/tickets/{id}/notes
     */
    @PostMapping("/{ticketNumber}/notes")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<?> addNote(
            @PathVariable String ticketNumber,
            @RequestBody Map<String, String> request) {
        
        java.util.Optional<SupportTicket> ticketOpt = supportTicketRepository.findByTicketNumber(ticketNumber);
        if (ticketOpt.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("Ticket not found"));
        }
        
        SupportTicket ticket = ticketOpt.get();
        String note = request.get("note");
        String addedBy = request.getOrDefault("addedBy", "AGENT");
        Boolean internal = Boolean.parseBoolean(request.getOrDefault("internal", "true"));
        
        ticket.addNote(note, addedBy, internal);
        ticket.setUpdatedAt(LocalDateTime.now());
        supportTicketRepository.save(ticket);
        
        return ResponseEntity.ok(ApiResponse.success("Note added successfully", ticket));
    }
    
    /**
     * Get Tickets by Status
     * GET /api/tickets/status/{status}
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<?> getTicketsByStatus(@PathVariable String status) {
        try {
            SupportTicket.TicketStatus ticketStatus = SupportTicket.TicketStatus.valueOf(status.toUpperCase());
            List<SupportTicket> tickets = supportTicketRepository.findByStatus(ticketStatus);
            return ResponseEntity.ok(ApiResponse.success(status + " tickets", tickets));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid status"));
        }
    }
    
    /**
     * Public endpoint to update ticket status (for agents without Spring Security)
     * PUT /api/public/tickets/{ticketNumber}/status
     */
    @PutMapping("/public/tickets/{ticketNumber}/status")
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
    
    // Helper methods
    private String generateTicketNumber() {
        return "TKT" + System.currentTimeMillis();
    }
    
    private IVROption.IssueCategory parseCategory(String category) {
        if (category == null) return null;
        try {
            return IVROption.IssueCategory.valueOf(category);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
