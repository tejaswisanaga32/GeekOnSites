package com.geekonsite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCustomerRequest {
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    private boolean verified;
    
    private String verificationNotes;
}
