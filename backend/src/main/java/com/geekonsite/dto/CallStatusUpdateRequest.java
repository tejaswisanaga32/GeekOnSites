package com.geekonsite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallStatusUpdateRequest {
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private String resolution;
    
    private String outcome;
    
    private boolean followUpRequired;
    
    private String followUpNotes;
}
