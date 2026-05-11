package com.geekonsite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentLoginRequest {
    
    @NotBlank(message = "Agent ID or Email is required")
    private String agentIdOrEmail;
    
    @NotBlank(message = "Password is required")
    private String password;
}
