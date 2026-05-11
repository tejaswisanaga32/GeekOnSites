package com.geekonsite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignAgentRequest {
    
    @NotBlank(message = "Call ID is required")
    private String callId;
    
    @NotBlank(message = "Agent ID is required")
    private String agentId;
}
