package com.geekonsite.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "ivr_options")
public class IVROption {
    
    @Id
    private String id;
    private String optionKey;
    private String displayText;
    private String description;
    private IssueCategory category;
    private Integer priority;
    private Boolean active;
    private String nextAction;
    private String troubleshootingFlowId;
    
    public enum IssueCategory {
        COMPUTER_ISSUE,
        PRINTER_ISSUE,
        NETWORK_WIFI,
        SOFTWARE_PROBLEM,
        VIRUS_MALWARE,
        DATA_RECOVERY,
        EMAIL_ISSUE,
        ACCOUNT_ACCESS,
        BILLING_INQUIRY,
        GENERAL_SUPPORT
    }
    
    public IVROption(String optionKey, String displayText, IssueCategory category, String nextAction) {
        this.optionKey = optionKey;
        this.displayText = displayText;
        this.category = category;
        this.nextAction = nextAction;
        this.active = true;
        this.priority = 0;
    }
}
