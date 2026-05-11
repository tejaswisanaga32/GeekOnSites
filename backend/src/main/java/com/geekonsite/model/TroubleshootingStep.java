package com.geekonsite.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "troubleshooting_steps")
public class TroubleshootingStep {
    
    @Id
    private String id;
    private String flowId;
    private String stepId;
    private String question;
    private String description;
    private List<Option> options;
    private String resolutionText;
    private String escalationReason;
    private Boolean isFinalStep;
    private Boolean requiresTechnician;
    private String remoteCommand;
    private String knowledgeBaseLink;
    
    @Data
    @NoArgsConstructor
    public static class Option {
        private String optionKey;
        private String displayText;
        private String nextStepId;
        private Boolean resolvesIssue;
        private String resolutionMessage;
    }
    
    public TroubleshootingStep(String flowId, String stepId, String question) {
        this.flowId = flowId;
        this.stepId = stepId;
        this.question = question;
        this.isFinalStep = false;
        this.requiresTechnician = false;
    }
}
