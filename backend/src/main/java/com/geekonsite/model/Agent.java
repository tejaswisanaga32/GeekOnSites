package com.geekonsite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "agents")
public class Agent {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String agentId;
    
    @Indexed(unique = true)
    private String email;
    
    private String password;
    
    private String firstName;
    
    private String lastName;
    
    private String phone;
    
    private String address;
    
    private String city;
    
    private String state;
    
    private String zipCode;
    
    private String experience;
    
    private String specialization;
    
    // Verification fields
    private boolean emailVerified;
    
    private boolean phoneVerified;
    
    private boolean idVerified;
    
    private boolean addressVerified;
    
    private String verificationStatus;
    
    private boolean active;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime lastLogin;
    
    public Agent(String agentId, String email, String password, String firstName, String lastName, String phone) {
        this.agentId = agentId;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }
}
