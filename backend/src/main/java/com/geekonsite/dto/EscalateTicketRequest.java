package com.geekonsite.dto;

import lombok.Data;

@Data
public class EscalateTicketRequest {
    private String ticketNumber;
    private String reason;
    private String notes;
    private Boolean remoteAttempted;
    private String escalationLevel;
}
