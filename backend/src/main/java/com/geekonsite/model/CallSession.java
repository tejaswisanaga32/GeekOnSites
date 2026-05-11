package com.geekonsite.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "call_sessions")
public class CallSession {
    
    @Id
    private String id;
    private String sessionId;
    private String phoneNumber;
    private String customerId;
    private CallStatus status;
    private SupportLevel currentLevel;
    private String ivrSelection;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer waitTimeSeconds;
    private Boolean resolved;
    private String resolutionType;
    private String notes;
    
    public CallSession(String sessionId, String phoneNumber) {
        this.sessionId = sessionId;
        this.phoneNumber = phoneNumber;
        this.status = CallStatus.IVR_MENU;
        this.currentLevel = SupportLevel.IVR;
        this.startTime = LocalDateTime.now();
        this.resolved = false;
    }
    
    public enum CallStatus {
        IVR_MENU,
        TROUBLESHOOTING,
        FIRST_LEVEL_SUPPORT,
        REMOTE_ASSISTANCE,
        ESCALATED,
        RESOLVED,
        ABANDONED
    }
    
    public enum SupportLevel {
        IVR,
        AUTO_TROUBLESHOOT,
        L1_SUPPORT,
        REMOTE_TECH,
        L2_TECHNICIAN
    }
}
