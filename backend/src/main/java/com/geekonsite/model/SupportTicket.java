package com.geekonsite.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "support_tickets")
public class SupportTicket {
    
    @Id
    private String id;
    private String ticketNumber;
    private String callSessionId;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private IVROption.IssueCategory category;
    private String issueDescription;
    private TicketStatus status;
    private SupportLevel supportLevel;
    private String assignedTo;
    private String assignedToName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private Boolean resolvedAtL1;
    private Boolean resolvedRemotely;
    private String resolution;
    private String troubleshootingSteps;
    private List<TicketNote> notes;
    private Integer satisfactionRating;
    private String customerFeedback;
    
    public enum TicketStatus {
        OPEN,
        IN_PROGRESS,
        WAITING_CUSTOMER,
        RESOLVED,
        CLOSED,
        ESCALATED
    }
    
    public enum SupportLevel {
        L1_FIRST_LINE,
        REMOTE_TECHNICIAN,
        L2_FIELD_TECH
    }
    
    @Data
    @NoArgsConstructor
    public static class TicketNote {
        private String note;
        private String addedBy;
        private LocalDateTime addedAt;
        private Boolean internal;
        
        public TicketNote(String note, String addedBy, Boolean internal) {
            this.note = note;
            this.addedBy = addedBy;
            this.addedAt = LocalDateTime.now();
            this.internal = internal;
        }
    }
    
    public SupportTicket(String ticketNumber, String callSessionId) {
        this.ticketNumber = ticketNumber;
        this.callSessionId = callSessionId;
        this.status = TicketStatus.OPEN;
        this.supportLevel = SupportLevel.L1_FIRST_LINE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.notes = new ArrayList<>();
        this.resolvedAtL1 = false;
        this.resolvedRemotely = false;
    }
    
    public void addNote(String note, String addedBy, Boolean internal) {
        if (this.notes == null) {
            this.notes = new ArrayList<>();
        }
        this.notes.add(new TicketNote(note, addedBy, internal));
        this.updatedAt = LocalDateTime.now();
    }
}
