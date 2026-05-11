package com.geekonsite.controller;

import com.geekonsite.dto.ApiResponse;
import com.geekonsite.model.SupportTicket;
import com.geekonsite.repository.SupportTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private SupportTicketRepository supportTicketRepository;
    
    /**
     * Get dashboard statistics
     * GET /api/admin/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getDashboardStats() {
        try {
            List<SupportTicket> allTickets = supportTicketRepository.findAll();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalTickets", allTickets.size());
            stats.put("openTickets", allTickets.stream().filter(t -> t.getStatus() == SupportTicket.TicketStatus.OPEN).count());
            stats.put("inProgressTickets", allTickets.stream().filter(t -> t.getStatus() == SupportTicket.TicketStatus.IN_PROGRESS).count());
            stats.put("resolvedTickets", allTickets.stream().filter(t -> t.getStatus() == SupportTicket.TicketStatus.RESOLVED).count());
            stats.put("closedTickets", allTickets.stream().filter(t -> t.getStatus() == SupportTicket.TicketStatus.CLOSED).count());
            
            return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to get dashboard stats: " + e.getMessage()));
        }
    }
    
    /**
     * Get all tickets
     * GET /api/admin/tickets
     */
    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse> getAllTickets() {
        try {
            List<SupportTicket> tickets = supportTicketRepository.findAll();
            return ResponseEntity.ok(ApiResponse.success("All tickets retrieved", tickets));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to get tickets: " + e.getMessage()));
        }
    }
    
    /**
     * Get tickets by status
     * GET /api/admin/tickets/status/{status}
     */
    @GetMapping("/tickets/status/{status}")
    public ResponseEntity<ApiResponse> getTicketsByStatus(@PathVariable String status) {
        try {
            SupportTicket.TicketStatus ticketStatus = SupportTicket.TicketStatus.valueOf(status.toUpperCase());
            List<SupportTicket> tickets = supportTicketRepository.findByStatus(ticketStatus);
            return ResponseEntity.ok(ApiResponse.success("Tickets by status retrieved", tickets));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid status: " + status));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to get tickets by status: " + e.getMessage()));
        }
    }
    
    /**
     * Update ticket status
     * PUT /api/admin/tickets/{ticketNumber}/status
     */
    @PutMapping("/tickets/{ticketNumber}/status")
    public ResponseEntity<ApiResponse> updateTicketStatus(
            @PathVariable String ticketNumber,
            @RequestBody Map<String, String> request) {
        try {
            SupportTicket ticket = supportTicketRepository.findByTicketNumber(ticketNumber)
                    .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketNumber));
            
            String status = request.get("status");
            String notes = request.getOrDefault("notes", "");
            
            SupportTicket.TicketStatus newStatus = SupportTicket.TicketStatus.valueOf(status.toUpperCase());
            ticket.setStatus(newStatus);
            
            if (!notes.isEmpty()) {
                ticket.addNote(notes, "admin", false);
            }
            
            supportTicketRepository.save(ticket);
            return ResponseEntity.ok(ApiResponse.success("Ticket status updated", ticket));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid status: " + request.get("status")));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to update ticket status: " + e.getMessage()));
        }
    }
    
    /**
     * Assign ticket to agent
     * PUT /api/admin/tickets/{ticketNumber}/assign
     */
    @PutMapping("/tickets/{ticketNumber}/assign")
    public ResponseEntity<ApiResponse> assignTicket(
            @PathVariable String ticketNumber,
            @RequestBody Map<String, String> request) {
        try {
            SupportTicket ticket = supportTicketRepository.findByTicketNumber(ticketNumber)
                    .orElseThrow(() -> new RuntimeException("Ticket not found: " + ticketNumber));
            
            String agentId = request.get("agentId");
            String agentName = request.get("agentName");
            
            ticket.setAssignedTo(agentId);
            ticket.setAssignedToName(agentName);
            ticket.setStatus(SupportTicket.TicketStatus.IN_PROGRESS);
            
            supportTicketRepository.save(ticket);
            return ResponseEntity.ok(ApiResponse.success("Ticket assigned successfully", ticket));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to assign ticket: " + e.getMessage()));
        }
    }
}
