package com.geekonsite.service;

import com.geekonsite.dto.*;
import com.geekonsite.model.*;
import com.geekonsite.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class SupportCallService {
    
    @Autowired
    private CallSessionRepository callSessionRepository;
    
    @Autowired
    private IVROptionRepository ivrOptionRepository;
    
    @Autowired
    private TroubleshootingStepRepository troubleshootingStepRepository;
    
    @Autowired
    private SupportTicketRepository supportTicketRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    /**
     * Step 1: Customer initiates a call - IVR System
     */
    public ApiResponse initiateCall(CallInitiateRequest request) {
        String sessionId = generateSessionId();
        
        CallSession session = new CallSession(sessionId, request.getPhoneNumber());
        if (request.getCustomerId() != null) {
            session.setCustomerId(request.getCustomerId());
        }
        
        callSessionRepository.save(session);
        log.info("New call session initiated: {} for phone: {}", sessionId, request.getPhoneNumber());
        
        // Check if customer exists
        Optional<Customer> existingCustomer = customerRepository.findByPhone(request.getPhoneNumber());
        boolean isExistingCustomer = existingCustomer.isPresent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("isExistingCustomer", isExistingCustomer);
        response.put("customerId", existingCustomer.map(Customer::getId).orElse(null));
        response.put("ivrOptions", getIVROptions());
        response.put("message", "Welcome to GeekOnSites Support. Please select an option.");
        
        return ApiResponse.success("Call initiated successfully", response);
    }
    
    /**
     * Get available IVR options
     */
    public List<IVROption> getIVROptions() {
        return ivrOptionRepository.findByActiveTrueOrderByPriorityAsc();
    }
    
    /**
     * Step 2: Handle IVR Selection and route to appropriate flow
     */
    public ApiResponse handleIVRSelection(IVRSelectionRequest request) {
        Optional<CallSession> sessionOpt = callSessionRepository.findBySessionId(request.getSessionId());
        if (sessionOpt.isEmpty()) {
            return ApiResponse.error("Session not found");
        }
        
        CallSession session = sessionOpt.get();
        session.setIvrSelection(request.getOptionKey());
        
        Optional<IVROption> option = ivrOptionRepository.findByOptionKey(request.getOptionKey());
        if (option.isEmpty()) {
            return ApiResponse.error("Invalid IVR option");
        }
        
        IVROption selectedOption = option.get();
        Map<String, Object> response = new HashMap<>();
        
        // Route based on category
        switch (selectedOption.getCategory()) {
            case BILLING_INQUIRY:
                // Billing inquiries go directly to L1
                session.setStatus(CallSession.CallStatus.FIRST_LEVEL_SUPPORT);
                session.setCurrentLevel(CallSession.SupportLevel.L1_SUPPORT);
                response.put("action", "CONNECT_L1");
                response.put("message", "Connecting you to our billing specialist...");
                break;
                
            case ACCOUNT_ACCESS:
                // Account issues may be resolved via automated system
                session.setStatus(CallSession.CallStatus.TROUBLESHOOTING);
                session.setCurrentLevel(CallSession.SupportLevel.AUTO_TROUBLESHOOT);
                response.put("action", "AUTO_ASSIST");
                response.put("troubleshootingFlow", selectedOption.getTroubleshootingFlowId());
                response.put("firstStep", getFirstTroubleshootingStep(selectedOption.getTroubleshootingFlowId()));
                break;
                
            case COMPUTER_ISSUE:
            case PRINTER_ISSUE:
            case NETWORK_WIFI:
            case SOFTWARE_PROBLEM:
                // Technical issues - start automated troubleshooting
                session.setStatus(CallSession.CallStatus.TROUBLESHOOTING);
                session.setCurrentLevel(CallSession.SupportLevel.AUTO_TROUBLESHOOT);
                response.put("action", "START_TROUBLESHOOTING");
                response.put("category", selectedOption.getCategory());
                response.put("troubleshootingFlow", selectedOption.getTroubleshootingFlowId());
                response.put("firstStep", getFirstTroubleshootingStep(selectedOption.getTroubleshootingFlowId()));
                response.put("message", "Let's try to resolve your " + selectedOption.getDisplayText() + " issue.");
                break;
                
            default:
                // General support - L1
                session.setStatus(CallSession.CallStatus.FIRST_LEVEL_SUPPORT);
                session.setCurrentLevel(CallSession.SupportLevel.L1_SUPPORT);
                response.put("action", "CONNECT_L1");
                response.put("message", "Connecting you to our support specialist...");
        }
        
        callSessionRepository.save(session);
        response.put("sessionId", session.getSessionId());
        response.put("currentStatus", session.getStatus());
        
        return ApiResponse.success("IVR selection processed", response);
    }
    
    /**
     * Step 3: Automated Troubleshooting Flow
     */
    public ApiResponse processTroubleshootingStep(TroubleshootingResponse request) {
        Optional<CallSession> sessionOpt = callSessionRepository.findBySessionId(request.getSessionId());
        if (sessionOpt.isEmpty()) {
            return ApiResponse.error("Session not found");
        }
        
        CallSession session = sessionOpt.get();
        
        // Get current step
        Optional<TroubleshootingStep> stepOpt = troubleshootingStepRepository
            .findByFlowIdAndStepId(session.getIvrSelection(), request.getStepId());
        
        if (stepOpt.isEmpty()) {
            return ApiResponse.error("Troubleshooting step not found");
        }
        
        TroubleshootingStep step = stepOpt.get();
        Map<String, Object> response = new HashMap<>();
        
        // Process the selected option
        for (TroubleshootingStep.Option option : step.getOptions()) {
            if (option.getOptionKey().equals(request.getSelectedOption())) {
                if (option.getResolvesIssue()) {
                    // Issue resolved through troubleshooting
                    session.setStatus(CallSession.CallStatus.RESOLVED);
                    session.setResolved(true);
                    session.setResolutionType("AUTO_TROUBLESHOOT");
                    session.setEndTime(LocalDateTime.now());
                    callSessionRepository.save(session);
                    
                    response.put("resolved", true);
                    response.put("resolutionMessage", option.getResolutionMessage());
                    response.put("action", "END_CALL");
                    
                    log.info("Issue resolved via troubleshooting for session: {}", session.getSessionId());
                    return ApiResponse.success("Issue resolved", response);
                } else {
                    // Move to next step or escalate
                    if (option.getNextStepId() != null) {
                        Optional<TroubleshootingStep> nextStep = troubleshootingStepRepository
                            .findByFlowIdAndStepId(session.getIvrSelection(), option.getNextStepId());
                        
                        if (nextStep.isPresent()) {
                            response.put("nextStep", nextStep.get());
                            response.put("action", "CONTINUE_TROUBLESHOOTING");
                        } else {
                            // No more steps, escalate to L1
                            escalateToL1(session);
                            response.put("action", "ESCALATE_L1");
                            response.put("message", "Connecting to first-level support...");
                        }
                    } else {
                        // End of flow, escalate
                        escalateToL1(session);
                        response.put("action", "ESCALATE_L1");
                        response.put("message", "Connecting to support specialist...");
                    }
                }
                break;
            }
        }
        
        callSessionRepository.save(session);
        response.put("sessionId", session.getSessionId());
        
        return ApiResponse.success("Troubleshooting step processed", response);
    }
    
    /**
     * Step 4: Create Support Ticket with captured details
     */
    public ApiResponse createTicket(CreateTicketRequest request) {
        Optional<CallSession> sessionOpt = callSessionRepository.findBySessionId(request.getSessionId());
        if (sessionOpt.isEmpty()) {
            return ApiResponse.error("Session not found");
        }
        
        CallSession session = sessionOpt.get();
        
        // Generate ticket number
        String ticketNumber = generateTicketNumber();
        
        SupportTicket ticket = new SupportTicket(ticketNumber, session.getSessionId());
        ticket.setCustomerName(request.getCustomerName());
        ticket.setCustomerPhone(request.getCustomerPhone());
        ticket.setCustomerEmail(request.getCustomerEmail());
        ticket.setIssueDescription(request.getIssueDescription());
        ticket.setTroubleshootingSteps(request.getTroubleshootingAttempted());
        
        // Determine category from IVR selection
        Optional<IVROption> option = ivrOptionRepository.findByOptionKey(session.getIvrSelection());
        option.ifPresent(ivrOption -> ticket.setCategory(ivrOption.getCategory()));
        
        // Check if remote resolution is possible
        boolean canResolveRemote = checkRemoteResolutionPossible(session.getIvrSelection());
        
        if (canResolveRemote) {
            ticket.setSupportLevel(SupportTicket.SupportLevel.REMOTE_TECHNICIAN);
            session.setStatus(CallSession.CallStatus.REMOTE_ASSISTANCE);
            session.setCurrentLevel(CallSession.SupportLevel.REMOTE_TECH);
        } else {
            ticket.setSupportLevel(SupportTicket.SupportLevel.L1_FIRST_LINE);
            session.setStatus(CallSession.CallStatus.FIRST_LEVEL_SUPPORT);
            session.setCurrentLevel(CallSession.SupportLevel.L1_SUPPORT);
        }
        
        ticket.addNote("Ticket created from call session: " + session.getSessionId(), "SYSTEM", true);
        
        supportTicketRepository.save(ticket);
        callSessionRepository.save(session);
        
        log.info("Support ticket created: {} for session: {}", ticketNumber, session.getSessionId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("ticketNumber", ticketNumber);
        response.put("supportLevel", ticket.getSupportLevel());
        response.put("canResolveRemote", canResolveRemote);
        response.put("message", canResolveRemote ? 
            "We can attempt to resolve this remotely. Connecting to remote technician..." :
            "Your ticket has been created. Connecting to first-level support...");
        
        return ApiResponse.success("Ticket created successfully", response);
    }
    
    /**
     * Step 5: Check remote resolution possibility
     */
    private boolean checkRemoteResolutionPossible(String issueCategory) {
        // Software, network, email issues can often be resolved remotely
        return issueCategory != null && (
            issueCategory.contains("SOFTWARE") ||
            issueCategory.contains("NETWORK") ||
            issueCategory.contains("EMAIL") ||
            issueCategory.contains("VIRUS")
        );
    }
    
    /**
     * Step 6: Provide software assistance (remote)
     */
    public ApiResponse attemptRemoteResolution(String ticketNumber, String resolutionNotes) {
        Optional<SupportTicket> ticketOpt = supportTicketRepository.findByTicketNumber(ticketNumber);
        if (ticketOpt.isEmpty()) {
            return ApiResponse.error("Ticket not found");
        }
        
        SupportTicket ticket = ticketOpt.get();
        
        // Mark as resolved remotely
        ticket.setResolvedRemotely(true);
        ticket.setResolution(resolutionNotes);
        ticket.setStatus(SupportTicket.TicketStatus.RESOLVED);
        ticket.setResolvedAt(LocalDateTime.now());
        ticket.addNote("Issue resolved remotely: " + resolutionNotes, "REMOTE_TECH", true);
        
        supportTicketRepository.save(ticket);
        
        // Update call session
        Optional<CallSession> sessionOpt = callSessionRepository.findBySessionId(ticket.getCallSessionId());
        sessionOpt.ifPresent(session -> {
            session.setStatus(CallSession.CallStatus.RESOLVED);
            session.setResolved(true);
            session.setResolutionType("REMOTE");
            session.setEndTime(LocalDateTime.now());
            callSessionRepository.save(session);
        });
        
        log.info("Ticket {} resolved remotely", ticketNumber);
        
        return ApiResponse.success("Remote resolution completed", ticket);
    }
    
    /**
     * Step 7: Escalate to technician if unresolved
     */
    public ApiResponse escalateToTechnician(EscalateTicketRequest request) {
        Optional<SupportTicket> ticketOpt = supportTicketRepository.findByTicketNumber(request.getTicketNumber());
        if (ticketOpt.isEmpty()) {
            return ApiResponse.error("Ticket not found");
        }
        
        SupportTicket ticket = ticketOpt.get();
        
        // Update ticket to escalated status
        ticket.setStatus(SupportTicket.TicketStatus.ESCALATED);
        ticket.setSupportLevel(SupportTicket.SupportLevel.L2_FIELD_TECH);
        ticket.addNote("Escalated to L2 Technician. Reason: " + request.getReason(), "L1_SUPPORT", true);
        
        if (request.getRemoteAttempted() != null && request.getRemoteAttempted()) {
            ticket.setResolvedRemotely(false);
            ticket.addNote("Remote resolution attempted but failed", "L1_SUPPORT", true);
        }
        
        supportTicketRepository.save(ticket);
        
        // Update call session
        Optional<CallSession> sessionOpt = callSessionRepository.findBySessionId(ticket.getCallSessionId());
        sessionOpt.ifPresent(session -> {
            session.setStatus(CallSession.CallStatus.ESCALATED);
            session.setCurrentLevel(CallSession.SupportLevel.L2_TECHNICIAN);
            callSessionRepository.save(session);
        });
        
        log.info("Ticket {} escalated to L2 technician. Reason: {}", 
            request.getTicketNumber(), request.getReason());
        
        Map<String, Object> response = new HashMap<>();
        response.put("ticketNumber", ticket.getTicketNumber());
        response.put("escalationLevel", "L2_FIELD_TECHNICIAN");
        response.put("message", "Your case has been escalated to a field technician. " +
            "A technician will contact you within 2 hours to schedule an on-site visit.");
        
        return ApiResponse.success("Ticket escalated successfully", response);
    }
    
    /**
     * Get ticket details
     */
    public ApiResponse getTicketDetails(String ticketNumber) {
        Optional<SupportTicket> ticket = supportTicketRepository.findByTicketNumber(ticketNumber);
        return ticket.map(value -> ApiResponse.success("Ticket found", value))
            .orElseGet(() -> ApiResponse.error("Ticket not found"));
    }
    
    /**
     * Get all active tickets for support dashboard
     */
    public List<SupportTicket> getActiveTickets() {
        return supportTicketRepository.findByStatus(SupportTicket.TicketStatus.OPEN);
    }
    
    /**
     * Get tickets by support level
     */
    public List<SupportTicket> getTicketsByLevel(SupportTicket.SupportLevel level) {
        return supportTicketRepository.findBySupportLevel(level);
    }
    
    /**
     * Assign ticket to agent
     */
    public ApiResponse assignTicket(String ticketNumber, String agentId, String agentName) {
        Optional<SupportTicket> ticketOpt = supportTicketRepository.findByTicketNumber(ticketNumber);
        if (ticketOpt.isEmpty()) {
            return ApiResponse.error("Ticket not found");
        }
        
        SupportTicket ticket = ticketOpt.get();
        ticket.setAssignedTo(agentId);
        ticket.setAssignedToName(agentName);
        ticket.setStatus(SupportTicket.TicketStatus.IN_PROGRESS);
        ticket.addNote("Assigned to " + agentName, "SYSTEM", true);
        
        supportTicketRepository.save(ticket);
        return ApiResponse.success("Ticket assigned", ticket);
    }
    
    /**
     * Update ticket status
     */
    public ApiResponse updateTicketStatus(String ticketNumber, SupportTicket.TicketStatus status, String notes) {
        Optional<SupportTicket> ticketOpt = supportTicketRepository.findByTicketNumber(ticketNumber);
        if (ticketOpt.isEmpty()) {
            return ApiResponse.error("Ticket not found");
        }
        
        SupportTicket ticket = ticketOpt.get();
        ticket.setStatus(status);
        if (notes != null) {
            ticket.addNote(notes, ticket.getAssignedToName(), true);
        }
        
        if (status == SupportTicket.TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
            
            // Close call session
            Optional<CallSession> sessionOpt = callSessionRepository.findBySessionId(ticket.getCallSessionId());
            sessionOpt.ifPresent(session -> {
                session.setStatus(CallSession.CallStatus.RESOLVED);
                session.setResolved(true);
                session.setEndTime(LocalDateTime.now());
                callSessionRepository.save(session);
            });
        }
        
        supportTicketRepository.save(ticket);
        return ApiResponse.success("Ticket status updated", ticket);
    }
    
    /**
     * Get support statistics
     */
    public Map<String, Object> getSupportStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalActiveCalls", callSessionRepository.countByStatusAndStartTimeAfter(
            CallSession.CallStatus.IVR_MENU, LocalDateTime.now().minusHours(24)));
        stats.put("openTickets", supportTicketRepository.countByStatus(SupportTicket.TicketStatus.OPEN));
        stats.put("inProgressTickets", supportTicketRepository.countByStatus(SupportTicket.TicketStatus.IN_PROGRESS));
        stats.put("escalatedTickets", supportTicketRepository.countByStatus(SupportTicket.TicketStatus.ESCALATED));
        stats.put("l1Tickets", supportTicketRepository.countBySupportLevelAndStatus(
            SupportTicket.SupportLevel.L1_FIRST_LINE, SupportTicket.TicketStatus.OPEN));
        stats.put("remoteTickets", supportTicketRepository.countBySupportLevelAndStatus(
            SupportTicket.SupportLevel.REMOTE_TECHNICIAN, SupportTicket.TicketStatus.OPEN));
        stats.put("resolvedAtL1", supportTicketRepository.findByResolvedAtL1True().size());
        stats.put("resolvedRemotely", supportTicketRepository.findByResolvedRemotelyTrue().size());
        
        return stats;
    }
    
    // Helper methods
    private String generateSessionId() {
        return "CALL" + System.currentTimeMillis();
    }
    
    private String generateTicketNumber() {
        return "TKT" + System.currentTimeMillis();
    }
    
    private TroubleshootingStep getFirstTroubleshootingStep(String flowId) {
        List<TroubleshootingStep> steps = troubleshootingStepRepository.findByFlowIdOrderByStepIdAsc(flowId);
        return steps.isEmpty() ? null : steps.get(0);
    }
    
    private void escalateToL1(CallSession session) {
        session.setStatus(CallSession.CallStatus.FIRST_LEVEL_SUPPORT);
        session.setCurrentLevel(CallSession.SupportLevel.L1_SUPPORT);
        session.setNotes("Escalated to L1 after failed troubleshooting");
        log.info("Session {} escalated to L1 support", session.getSessionId());
    }
}
