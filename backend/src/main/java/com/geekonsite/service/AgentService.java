package com.geekonsite.service;

import com.geekonsite.dto.*;
import com.geekonsite.model.Agent;
import com.geekonsite.model.Call;
import com.geekonsite.repository.AgentRepository;
import com.geekonsite.repository.CallRepository;
import com.geekonsite.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AgentService {
    
    @Autowired
    private AgentRepository agentRepository;
    
    @Autowired
    private CallRepository callRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    public ApiResponse registerAgent(AgentRegistrationRequest request) {
        if (agentRepository.existsByAgentId(request.getAgentId())) {
            return ApiResponse.error("Agent ID already exists");
        }
        
        if (agentRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.error("Email already registered");
        }
        
        Agent agent = new Agent(
            request.getAgentId(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getFirstName(),
            request.getLastName(),
            request.getPhone()
        );
        
        agentRepository.save(agent);
        return ApiResponse.success("Agent registered successfully");
    }
    
    public ApiResponse login(AgentLoginRequest request) {
        try {
            String username = request.getAgentIdOrEmail();
            String password = request.getPassword();
            
            Optional<Agent> agentOpt = agentRepository.findByAgentId(username);
            if (agentOpt.isEmpty()) {
                agentOpt = agentRepository.findByEmail(username);
            }
            
            if (agentOpt.isPresent()) {
                Agent agent = agentOpt.get();
                
                // Simple password validation
                if (passwordEncoder.matches(password, agent.getPassword())) {
                    agent.setLastLogin(LocalDateTime.now());
                    agentRepository.save(agent);
                    
                    // Generate simple token
                    String token = "agent-token-" + agent.getAgentId() + "-" + System.currentTimeMillis();
                    
                    LoginResponse response = new LoginResponse(
                        token,
                        agent.getId(),
                        agent.getAgentId(),
                        agent.getEmail(),
                        agent.getFirstName(),
                        agent.getLastName(),
                        List.of("AGENT")
                    );
                    
                    return ApiResponse.success("Login successful", response);
                }
            }
            
            return ApiResponse.error("Invalid credentials");
        } catch (Exception e) {
            return ApiResponse.error("Login failed: " + e.getMessage());
        }
    }
    
    public ApiResponse getAgentDashboard(String agentId) {
        Optional<Agent> agentOpt = agentRepository.findByAgentId(agentId);
        if (agentOpt.isEmpty()) {
            return ApiResponse.error("Agent not found");
        }
        
        List<Call> pendingCalls = callRepository.findByAgentIdAndStatus(agentId, "PENDING");
        List<Call> inProgressCalls = callRepository.findByAgentIdAndStatus(agentId, "IN_PROGRESS");
        List<Call> completedCalls = callRepository.findByAgentIdAndStatus(agentId, "COMPLETED");
        List<Call> assignedCalls = callRepository.findByAgentIdAndStatusNot(agentId, "COMPLETED");
        
        DashboardStats stats = new DashboardStats();
        stats.setPendingCalls(pendingCalls.size());
        stats.setInProgressCalls(inProgressCalls.size());
        stats.setCompletedCalls(completedCalls.size());
        stats.setTotalCalls(pendingCalls.size() + inProgressCalls.size() + completedCalls.size());
        
        AgentDashboardResponse response = new AgentDashboardResponse(
            agentOpt.get(),
            stats,
            pendingCalls,
            inProgressCalls,
            completedCalls,
            assignedCalls
        );
        
        return ApiResponse.success("Dashboard data retrieved", response);
    }
    
    public ApiResponse getAllActiveAgents() {
        List<Agent> agents = agentRepository.findByActiveTrue();
        List<AgentInfo> agentInfoList = agents.stream()
            .map(a -> new AgentInfo(a.getId(), a.getAgentId(), a.getFirstName() + " " + a.getLastName(), a.getEmail()))
            .collect(Collectors.toList());
        return ApiResponse.success("Active agents retrieved", agentInfoList);
    }
    
    public Optional<Agent> findByAgentId(String agentId) {
        return agentRepository.findByAgentId(agentId);
    }
    
    // Inner class for agent info
    public record AgentInfo(String id, String agentId, String fullName, String email) {}
    
    // Inner class for dashboard response
    public record AgentDashboardResponse(
        Agent agent,
        DashboardStats stats,
        List<Call> pendingCalls,
        List<Call> inProgressCalls,
        List<Call> completedCalls,
        List<Call> assignedCalls
    ) {}
}
