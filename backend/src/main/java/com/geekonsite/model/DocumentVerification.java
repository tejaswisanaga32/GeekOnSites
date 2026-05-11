package com.geekonsite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVerification {
    
    private String documentType;
    
    private String documentNumber;
    
    private String documentFileUrl;
    
    private boolean verified;
    
    private String verificationNotes;
    
    public DocumentVerification(String documentType, String documentNumber, String documentFileUrl) {
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.documentFileUrl = documentFileUrl;
        this.verified = false;
    }
}
