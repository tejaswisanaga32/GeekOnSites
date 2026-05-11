package com.geekonsite.dto;

import lombok.Data;

@Data
public class CreateTicketRequest {
    private String sessionId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String issueDescription;
    private String troubleshootingAttempted;
}
