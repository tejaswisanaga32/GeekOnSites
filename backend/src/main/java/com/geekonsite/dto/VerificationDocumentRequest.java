package com.geekonsite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationDocumentRequest {
    
    @NotBlank(message = "Document type is required")
    private String documentType;
    
    @NotBlank(message = "Document number is required")
    private String documentNumber;
    
    private String documentFileBase64;
    
    private String documentFileName;
}
