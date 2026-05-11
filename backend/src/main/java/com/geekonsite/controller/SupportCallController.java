package com.geekonsite.controller;

import com.geekonsite.dto.*;
import com.geekonsite.model.SupportTicket;
import com.geekonsite.service.SupportCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
@CrossOrigin(origins = "*")
public class SupportCallController {
    
    @Autowired
    private SupportCallService supportCallService;
    
    /**
     * Step 1: Customer initiates a call (IVR System)
     * Public endpoint - no authentication required
     */
    @PostMapping("/call/initiate")
    public ResponseEntity<?> initiateCall(@RequestBody CallInitiateRequest request) {
        return ResponseEntity.ok(supportCallService.initiateCall(request));
    }
    
    /**
     * Step 2: Handle IVR Selection
     * Public endpoint
     */
    @PostMapping("/call/ivr/select")
    public ResponseEntity<?> handleIVRSelection(@RequestBody IVRSelectionRequest request) {
        return ResponseEntity.ok(supportCallService.handleIVRSelection(request));
    }
    
    /**
     * Get IVR Menu Options
     * Public endpoint
     */
    @GetMapping("/ivr/options")
    public ResponseEntity<?> getIVROptions() {
        return ResponseEntity.ok(supportCallService.getIVROptions());
    }
    
    /**
     * Step 3: Automated Troubleshooting
     * Public endpoint
     */
    @PostMapping("/call/troubleshoot")
    public ResponseEntity<?> processTroubleshooting(@RequestBody TroubleshootingResponse request) {
        return ResponseEntity.ok(supportCallService.processTroubleshootingStep(request));
    }
    
    /**
     * Step 4: Create Support Ticket
     * Public endpoint
     */
    @PostMapping("/ticket/create")
    public ResponseEntity<?> createTicket(@RequestBody CreateTicketRequest request) {
        return ResponseEntity.ok(supportCallService.createTicket(request));
    }
    
    /**
     * Step 5 & 6: Attempt Remote Resolution
     * Requires agent/technician authentication
     */
    @PostMapping("/ticket/{ticketNumber}/remote-resolve")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'TECH')")
    public ResponseEntity<?> attemptRemoteResolution(
            @PathVariable String ticketNumber,
            @RequestBody Map<String, String> request) {
        String notes = request.getOrDefault("resolutionNotes", "");
        return ResponseEntity.ok(supportCallService.attemptRemoteResolution(ticketNumber, notes));
    }
    
    /**
     * Step 7: Escalate to Technician
     * Requires L1 support or admin authentication
     */
    @PostMapping("/ticket/escalate")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<?> escalateToTechnician(@RequestBody EscalateTicketRequest request) {
        return ResponseEntity.ok(supportCallService.escalateToTechnician(request));
    }
    
    /**
     * Get Ticket Details
     * Any authenticated user
     */
    @GetMapping("/ticket/{ticketNumber}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'AGENT', 'ADMIN', 'TECH')")
    public ResponseEntity<?> getTicketDetails(@PathVariable String ticketNumber) {
        return ResponseEntity.ok(supportCallService.getTicketDetails(ticketNumber));
    }
    
    /**
     * Get Active Tickets (Dashboard)
     * Requires agent/admin authentication
     */
    @GetMapping("/tickets/active")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<?> getActiveTickets() {
        List<SupportTicket> tickets = supportCallService.getActiveTickets();
        return ResponseEntity.ok(ApiResponse.success("Active tickets retrieved", tickets));
    }
    
    /**
     * Get Tickets by Support Level
     * Requires agent/admin authentication
     */
    @GetMapping("/tickets/level/{level}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<?> getTicketsByLevel(@PathVariable String level) {
        try {
            SupportTicket.SupportLevel supportLevel = SupportTicket.SupportLevel.valueOf(level);
            List<SupportTicket> tickets = supportCallService.getTicketsByLevel(supportLevel);
            return ResponseEntity.ok(ApiResponse.success("Tickets retrieved", tickets));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid support level"));
        }
    }
    
    /**
     * Assign Ticket to Agent
     * Requires admin authentication
     */
    @PostMapping("/ticket/{ticketNumber}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignTicket(
            @PathVariable String ticketNumber,
            @RequestBody Map<String, String> request) {
        String agentId = request.get("agentId");
        String agentName = request.get("agentName");
        return ResponseEntity.ok(supportCallService.assignTicket(ticketNumber, agentId, agentName));
    }
    
    /**
     * Update Ticket Status
     * Requires agent/admin authentication
     */
    @PutMapping("/ticket/{ticketNumber}/status")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<?> updateTicketStatus(
            @PathVariable String ticketNumber,
            @RequestBody Map<String, String> request) {
        try {
            SupportTicket.TicketStatus status = SupportTicket.TicketStatus.valueOf(request.get("status"));
            String notes = request.getOrDefault("notes", null);
            return ResponseEntity.ok(supportCallService.updateTicketStatus(ticketNumber, status, notes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid ticket status"));
        }
    }
    
    /**
     * Get Support Statistics
     * Requires admin authentication
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getSupportStats() {
        Map<String, Object> stats = supportCallService.getSupportStats();
        return ResponseEntity.ok(ApiResponse.success("Support statistics", stats));
    }
    
    /**
     * L1 Support Dashboard - Get L1 Queue
     * Requires L1 agent authentication
     */
    @GetMapping("/l1/queue")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<?> getL1Queue() {
        List<SupportTicket> tickets = supportCallService.getTicketsByLevel(SupportTicket.SupportLevel.L1_FIRST_LINE);
        return ResponseEntity.ok(ApiResponse.success("L1 queue", tickets));
    }
    
    /**
     * Remote Support Dashboard - Get Remote Queue
     * Requires remote technician authentication
     */
    @GetMapping("/remote/queue")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'TECH')")
    public ResponseEntity<?> getRemoteQueue() {
        List<SupportTicket> tickets = supportCallService.getTicketsByLevel(SupportTicket.SupportLevel.REMOTE_TECHNICIAN);
        return ResponseEntity.ok(ApiResponse.success("Remote queue", tickets));
    }
    
    /**
     * L2 Field Technician Dashboard - Get Escalated Tickets
     * Requires L2 technician authentication
     */
    @GetMapping("/l2/escalated")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECH')")
    public ResponseEntity<?> getEscalatedTickets() {
        List<SupportTicket> tickets = supportCallService.getTicketsByLevel(SupportTicket.SupportLevel.L2_FIELD_TECH);
        return ResponseEntity.ok(ApiResponse.success("Escalated tickets", tickets));
    }
}
