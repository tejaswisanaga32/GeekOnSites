package com.geekonsite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    
    private long totalCalls;
    
    private long pendingCalls;
    
    private long inProgressCalls;
    
    private long completedCalls;
    
    private long totalCustomers;
    
    private long totalAgents;
    
    private long verifiedCustomers;
    
    private long pendingVerificationCustomers;
}
