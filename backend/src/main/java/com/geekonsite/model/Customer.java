package com.geekonsite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customers")
public class Customer {
    
    @Id
    private String id;
    
    private String customerId;
    
    private String firstName;
    
    private String lastName;
    
    @Indexed(unique = true)
    private String email;
    
    private String phone;
    
    private String address;
    
    private String city;
    
    private String state;
    
    private String zipCode;
    
    private List<DocumentVerification> verificationDocuments;
    
    private String verificationStatus;
    
    private boolean verified;
    
    private LocalDateTime registeredAt;
    
    private LocalDateTime updatedAt;
    
    public Customer(String firstName, String lastName, String email, String phone) {
        this.customerId = "CUST" + System.currentTimeMillis();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.verificationDocuments = new ArrayList<>();
        this.verificationStatus = "PENDING";
        this.verified = false;
        this.registeredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addVerificationDocument(DocumentVerification document) {
        if (this.verificationDocuments == null) {
            this.verificationDocuments = new ArrayList<>();
        }
        this.verificationDocuments.add(document);
        this.updatedAt = LocalDateTime.now();
    }
}
