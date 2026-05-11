package com.geekonsite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "calls")
public class Call {
    
    @Id
    private String id;
    
    private String callId;
    
    private String customerId;
    
    private String customerName;
    
    private String customerPhone;
    
    private String customerEmail;
    
    private String agentId;
    
    private String agentName;
    
    private String status;
    
    private String priority;
    
    private String issueDescription;
    
    private String resolution;
    
    private String outcome;
    
    private boolean followUpRequired;
    
    private String followUpNotes;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime assignedAt;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    private LocalDateTime updatedAt;
    
    public Call(String customerId, String customerName, String customerPhone, String customerEmail, 
                String issueDescription, String priority) {
        this.callId = "CALL" + System.currentTimeMillis();
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.issueDescription = issueDescription;
        this.priority = priority != null ? priority : "MEDIUM";
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
