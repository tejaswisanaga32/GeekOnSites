package com.geekonsite.controller;

import com.geekonsite.dto.ApiResponse;
import com.geekonsite.model.Agent;
import com.geekonsite.model.SupportTicket;
import com.geekonsite.repository.AgentRepository;
import com.geekonsite.repository.SupportTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/agent")
@CrossOrigin(origins = "*")
public class AgentController {
    
    @Autowired
    private AgentRepository agentRepository;
    
    @Autowired
    private SupportTicketRepository supportTicketRepository;
    
    /**
     * Agent login endpoint
     * POST /api/agent/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Username and password required"));
        }
        
        // Find agent by agentId or email
        Optional<Agent> agentOpt = agentRepository.findByAgentId(username);
        if (agentOpt.isEmpty()) {
            agentOpt = agentRepository.findByEmail(username);
        }
        
        if (agentOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Agent not found"));
        }
        
        Agent agent = agentOpt.get();
        
        // Simple password check
        if (!password.equals(agent.getPassword())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid credentials"));
        }
        
        // Generate simple token
        String token = "agent-token-" + agent.getAgentId() + "-" + System.currentTimeMillis();
        
        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("agent", Map.of(
            "id", agent.getId(),
            "agentId", agent.getAgentId(),
            "email", agent.getEmail(),
            "firstName", agent.getFirstName(),
            "lastName", agent.getLastName(),
            "phone", agent.getPhone()
        ));
        
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
    
    /**
     * Enhanced agent registration with verification
     * POST /api/agent/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerAgent(@RequestBody Map<String, Object> registrationRequest) {
        try {
            // Extract basic information
            String firstName = (String) registrationRequest.get("firstName");
            String lastName = (String) registrationRequest.get("lastName");
            String email = (String) registrationRequest.get("email");
            String phone = (String) registrationRequest.get("phone");
            String address = (String) registrationRequest.get("address");
            String city = (String) registrationRequest.get("city");
            String state = (String) registrationRequest.get("state");
            String zipCode = (String) registrationRequest.get("zipCode");
            String experience = (String) registrationRequest.get("experience");
            String specialization = (String) registrationRequest.get("specialization");
            String verificationType = (String) registrationRequest.get("verificationType");
            Map<String, Object> verificationDocument = (Map<String, Object>) registrationRequest.get("verificationDocument");
            
            // Validate verification document
            if (verificationType.equals("id") && verificationDocument != null) {
                String docType = (String) verificationDocument.get("type");
                if (docType.equals("SSN")) {
                    String ssnNumber = (String) verificationDocument.get("ssnNumber");
                    if (ssnNumber == null || ssnNumber.length() != 11) {
                        return ResponseEntity.badRequest().body(ApiResponse.error("Invalid SSN format"));
                    }
                } else if (docType.equals("ID_DOCUMENT")) {
                    Object file = verificationDocument.get("file");
                    if (file == null) {
                        return ResponseEntity.badRequest().body(ApiResponse.error("ID document file is required"));
                    }
                }
            }
            
            // Check if agent already exists
            Optional<Agent> existingAgent = agentRepository.findByEmail(email);
            if (existingAgent.isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Agent with this email already exists"));
            }
            
            // Create new agent
            Agent agent = new Agent();
            agent.setFirstName(firstName);
            agent.setLastName(lastName);
            agent.setEmail(email);
            agent.setPhone(phone);
            agent.setAddress(address);
            agent.setCity(city);
            agent.setState(state);
            agent.setZipCode(zipCode);
            agent.setExperience(experience);
            agent.setSpecialization(specialization);
            
            // Generate agent ID
            String agentId = "AGT" + System.currentTimeMillis();
            agent.setAgentId(agentId);
            
            // Set default password (in production, this should be generated and sent via email)
            agent.setPassword("tempPassword123");
            
            // Set verification status based on submitted document
            agent.setEmailVerified(verificationType.equals("email"));
            agent.setPhoneVerified(verificationType.equals("phone"));
            agent.setIdVerified(verificationType.equals("id"));
            agent.setAddressVerified(verificationType.equals("address"));
            agent.setVerificationStatus("VERIFIED"); // Single document verified
            
            // Save agent
            agentRepository.save(agent);
            
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("agentId", agentId);
            response.put("message", "Registration submitted successfully. Your application is under review.");
            response.put("nextSteps", "Complete email and phone verification, upload ID and address documents");
            
            return ResponseEntity.ok(ApiResponse.success("Agent registration submitted successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to register agent: " + e.getMessage()));
        }
    }
    
    /**
     * Get all agents (for admin dropdown)
     * GET /api/agent/list
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse> getAllAgents() {
        try {
            List<Agent> agents = agentRepository.findAll();
            return ResponseEntity.ok(ApiResponse.success("Agents retrieved", agents));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to get agents: " + e.getMessage()));
        }
    }
    
    /**
     * Get agent's dashboard with assigned tickets
     * GET /api/agent/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@RequestHeader("Authorization") String authorization) {
        try {
            String agentId = extractAgentIdFromToken(authorization);
            if (agentId == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Invalid agent token"));
            }
            
            // Get tickets assigned to this agent
            List<SupportTicket> assignedTickets = supportTicketRepository.findByAssignedTo(agentId);
            
            // Calculate stats
            long pendingCount = assignedTickets.stream().filter(t -> t.getStatus() == SupportTicket.TicketStatus.OPEN).count();
            long inProgressCount = assignedTickets.stream().filter(t -> t.getStatus() == SupportTicket.TicketStatus.IN_PROGRESS).count();
            long completedCount = assignedTickets.stream().filter(t -> t.getStatus() == SupportTicket.TicketStatus.RESOLVED || t.getStatus() == SupportTicket.TicketStatus.CLOSED).count();
            
            Map<String, Object> dashboard = Map.of(
                "agentId", agentId,
                "totalTickets", assignedTickets.size(),
                "pendingTickets", pendingCount,
                "inProgressTickets", inProgressCount,
                "completedTickets", completedCount,
                "assignedTickets", assignedTickets
            );
            
            return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved", dashboard));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to get dashboard: " + e.getMessage()));
        }
    }
    
    /**
     * Update ticket status
     * PUT /api/agent/tickets/{ticketNumber}/status
     */
    @PutMapping("/tickets/{ticketNumber}/status")
    public ResponseEntity<?> updateTicketStatus(
            @PathVariable String ticketNumber,
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authorization) {
        try {
            String agentId = extractAgentIdFromToken(authorization);
            if (agentId == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Invalid agent token"));
            }
            
            Optional<SupportTicket> ticketOpt = supportTicketRepository.findByTicketNumber(ticketNumber);
            if (ticketOpt.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("Ticket not found"));
            }
            
            SupportTicket ticket = ticketOpt.get();
            
            // Verify ticket is assigned to this agent
            if (!agentId.equals(ticket.getAssignedTo())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Ticket not assigned to this agent"));
            }
            
            // Update ticket
            String status = (String) request.get("status");
            String notes = (String) request.get("notes");
            
            SupportTicket.TicketStatus newStatus = SupportTicket.TicketStatus.valueOf(status.toUpperCase());
            ticket.setStatus(newStatus);
            ticket.setUpdatedAt(LocalDateTime.now());
            
            if (notes != null && !notes.trim().isEmpty()) {
                ticket.addNote(notes, "agent", false);
            }
            
            supportTicketRepository.save(ticket);
            
            return ResponseEntity.ok(ApiResponse.success("Ticket status updated successfully", ticket));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to update ticket: " + e.getMessage()));
        }
    }
    
    /**
     * Extract agent ID from token
     */
    private String extractAgentIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        
        String token = authorization.replace("Bearer ", "");
        if (!token.startsWith("agent-token-")) {
            return null;
        }
        
        String[] tokenParts = token.split("-");
        if (tokenParts.length < 3) {
            return null;
        }
        
        return tokenParts[2];
    }
}
