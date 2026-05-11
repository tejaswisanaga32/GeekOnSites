package com.geekonsite.dto;

import lombok.Data;

@Data
public class CallInitiateRequest {
    private String phoneNumber;
    private String customerId;
}
