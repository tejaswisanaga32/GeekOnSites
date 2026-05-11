package com.geekonsite.controller;

import com.geekonsite.dto.*;
import com.geekonsite.service.CallService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calls")
@CrossOrigin(origins = "*")
public class CallController {
    
    @Autowired
    private CallService callService;
    
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createCall(@Valid @RequestBody CallAllocationRequest request) {
        ApiResponse response = callService.createCall(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> assignAgent(@Valid @RequestBody AssignAgentRequest request) {
        ApiResponse response = callService.assignAgentToCall(request);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{callId}/status")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateStatus(
            @PathVariable String callId,
            @Valid @RequestBody CallStatusUpdateRequest request) {
        ApiResponse response = callService.updateCallStatus(callId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{callId}")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getCallById(@PathVariable String callId) {
        ApiResponse response = callService.getCallById(callId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getCallsByStatus(@PathVariable String status) {
        ApiResponse response = callService.getCallsByStatus(status);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getPendingCalls() {
        ApiResponse response = callService.getPendingCalls();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllCalls() {
        ApiResponse response = callService.getAllCalls();
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{callId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteCall(@PathVariable String callId) {
        ApiResponse response = callService.deleteCall(callId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getCallStats() {
        ApiResponse response = callService.getCallStats();
        return ResponseEntity.ok(response);
    }
}
