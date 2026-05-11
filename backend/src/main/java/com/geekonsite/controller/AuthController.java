package com.geekonsite.controller;

import com.geekonsite.dto.*;
import com.geekonsite.service.AgentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AgentService agentService;
    
    @PostMapping("/agent/login")
    public ResponseEntity<ApiResponse> agentLogin(@Valid @RequestBody AgentLoginRequest request) {
        ApiResponse response = agentService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/agent/register")
    public ResponseEntity<ApiResponse> agentRegister(@Valid @RequestBody AgentRegistrationRequest request) {
        ApiResponse response = agentService.registerAgent(request);
        return ResponseEntity.ok(response);
    }
}
