package com.geekonsite.dto;

import lombok.Data;

@Data
public class IVRSelectionRequest {
    private String sessionId;
    private String optionKey;
    private String phoneNumber;
}
