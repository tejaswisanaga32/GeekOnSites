package com.geekonsite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    
    private String type = "Bearer";
    
    private String id;
    
    private String username;
    
    private String email;
    
    private String firstName;
    
    private String lastName;
    
    private List<String> roles;
    
    public LoginResponse(String token, String id, String username, String email, 
                         String firstName, String lastName, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }
}
