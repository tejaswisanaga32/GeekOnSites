package com.geekonsite.controller;

import com.geekonsite.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicController {
    
    @GetMapping("/test")
    public ResponseEntity<ApiResponse> testPublicAccess() {
        return ResponseEntity.ok(ApiResponse.success("Public access works!"));
    }
}
