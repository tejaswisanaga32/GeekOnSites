package com.geekonsite.service;

import com.geekonsite.dto.*;
import com.geekonsite.model.Agent;
import com.geekonsite.model.Call;
import com.geekonsite.model.Customer;
import com.geekonsite.repository.AgentRepository;
import com.geekonsite.repository.CallRepository;
import com.geekonsite.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CallService {
    
    @Autowired
    private CallRepository callRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private AgentRepository agentRepository;
    
    public ApiResponse createCall(CallAllocationRequest request) {
        Optional<Customer> customerOpt = customerRepository.findByCustomerId(request.getCustomerId());
        if (customerOpt.isEmpty()) {
            return ApiResponse.error("Customer not found");
        }
        
        Customer customer = customerOpt.get();
        
        Call call = new Call(
            customer.getCustomerId(),
            customer.getFirstName() + " " + customer.getLastName(),
            customer.getPhone(),
            customer.getEmail(),
            request.getIssueDescription(),
            request.getPriority()
        );
        
        Call savedCall = callRepository.save(call);
        return ApiResponse.success("Call created successfully", savedCall);
    }
    
    public ApiResponse assignAgentToCall(AssignAgentRequest request) {
        Optional<Call> callOpt = callRepository.findByCallId(request.getCallId());
        if (callOpt.isEmpty()) {
            return ApiResponse.error("Call not found");
        }
        
        Optional<Agent> agentOpt = agentRepository.findByAgentId(request.getAgentId());
        if (agentOpt.isEmpty()) {
            return ApiResponse.error("Agent not found");
        }
        
        Call call = callOpt.get();
        Agent agent = agentOpt.get();
        
        call.setAgentId(agent.getAgentId());
        call.setAgentName(agent.getFirstName() + " " + agent.getLastName());
        call.setStatus("PENDING");
        call.setAssignedAt(LocalDateTime.now());
        call.setUpdatedAt(LocalDateTime.now());
        
        Call savedCall = callRepository.save(call);
        return ApiResponse.success("Call assigned to agent successfully", savedCall);
    }
    
    public ApiResponse updateCallStatus(String callId, CallStatusUpdateRequest request) {
        Optional<Call> callOpt = callRepository.findByCallId(callId);
        if (callOpt.isEmpty()) {
            return ApiResponse.error("Call not found");
        }
        
        Call call = callOpt.get();
        String oldStatus = call.getStatus();
        String newStatus = request.getStatus();
        
        call.setStatus(newStatus);
        call.setUpdatedAt(LocalDateTime.now());
        
        if ("IN_PROGRESS".equals(newStatus) && !"IN_PROGRESS".equals(oldStatus)) {
            call.setStartedAt(LocalDateTime.now());
        }
        
        if ("COMPLETED".equals(newStatus)) {
            call.setCompletedAt(LocalDateTime.now());
            call.setResolution(request.getResolution());
            call.setOutcome(request.getOutcome());
            call.setFollowUpRequired(request.isFollowUpRequired());
            call.setFollowUpNotes(request.getFollowUpNotes());
        }
        
        Call savedCall = callRepository.save(call);
        return ApiResponse.success("Call status updated successfully", savedCall);
    }
    
    public ApiResponse getCallById(String callId) {
        Optional<Call> callOpt = callRepository.findByCallId(callId);
        if (callOpt.isPresent()) {
            return ApiResponse.success("Call found", callOpt.get());
        }
        return ApiResponse.error("Call not found");
    }
    
    public ApiResponse getCallsByAgent(String agentId) {
        List<Call> calls = callRepository.findByAgentId(agentId);
        return ApiResponse.success("Agent calls retrieved", calls);
    }
    
    public ApiResponse getCallsByStatus(String status) {
        List<Call> calls = callRepository.findByStatus(status);
        return ApiResponse.success("Calls retrieved", calls);
    }
    
    public ApiResponse getPendingCalls() {
        List<Call> calls = callRepository.findByStatusOrderByCreatedAtAsc("PENDING");
        return ApiResponse.success("Pending calls retrieved", calls);
    }
    
    public ApiResponse getAllCalls() {
        List<Call> calls = callRepository.findAll();
        return ApiResponse.success("All calls retrieved", calls);
    }
    
    public ApiResponse deleteCall(String callId) {
        Optional<Call> callOpt = callRepository.findByCallId(callId);
        if (callOpt.isEmpty()) {
            return ApiResponse.error("Call not found");
        }
        
        callRepository.delete(callOpt.get());
        return ApiResponse.success("Call deleted successfully");
    }
    
    public ApiResponse getCallStats() {
        DashboardStats stats = new DashboardStats();
        stats.setTotalCalls(callRepository.count());
        stats.setPendingCalls(callRepository.countByStatus("PENDING"));
        stats.setInProgressCalls(callRepository.countByStatus("IN_PROGRESS"));
        stats.setCompletedCalls(callRepository.countByStatus("COMPLETED"));
        stats.setTotalCustomers(customerRepository.count());
        stats.setTotalAgents(agentRepository.count());
        stats.setVerifiedCustomers(customerRepository.findByVerifiedTrue().size());
        stats.setPendingVerificationCustomers(customerRepository.findByVerifiedFalse().size());
        
        return ApiResponse.success("Call statistics retrieved", stats);
    }
}
