package com.geekonsite.controller;

import com.geekonsite.dto.*;
import com.geekonsite.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        ApiResponse response = customerService.registerCustomer(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{customerId}")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getCustomerById(@PathVariable String customerId) {
        ApiResponse response = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getCustomerByEmail(@PathVariable String email) {
        ApiResponse response = customerService.getCustomerByEmail(email);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllCustomers() {
        ApiResponse response = customerService.getAllCustomers();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/verified")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getVerifiedCustomers() {
        ApiResponse response = customerService.getVerifiedCustomers();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/pending-verification")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getPendingVerificationCustomers() {
        ApiResponse response = customerService.getPendingVerificationCustomers();
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> verifyCustomer(@RequestBody VerifyCustomerRequest request) {
        ApiResponse response = customerService.verifyCustomer(request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteCustomer(@PathVariable String customerId) {
        ApiResponse response = customerService.deleteCustomer(customerId);
        return ResponseEntity.ok(response);
    }
}
