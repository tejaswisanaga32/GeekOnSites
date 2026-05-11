package com.geekonsite.dto;

import lombok.Data;

@Data
public class TroubleshootingResponse {
    private String sessionId;
    private String stepId;
    private String selectedOption;
    private String notes;
}
