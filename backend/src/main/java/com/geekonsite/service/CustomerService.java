package com.geekonsite.service;

import com.geekonsite.dto.ApiResponse;
import com.geekonsite.dto.CustomerRegistrationRequest;
import com.geekonsite.dto.VerificationDocumentRequest;
import com.geekonsite.dto.VerifyCustomerRequest;
import com.geekonsite.model.Customer;
import com.geekonsite.model.DocumentVerification;
import com.geekonsite.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    public ApiResponse registerCustomer(CustomerRegistrationRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.error("Email already registered");
        }
        
        Customer customer = new Customer(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPhone()
        );
        
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setZipCode(request.getZipCode());
        
        // Process verification documents
        if (request.getVerificationDocuments() != null && !request.getVerificationDocuments().isEmpty()) {
            for (VerificationDocumentRequest docRequest : request.getVerificationDocuments()) {
                String fileUrl = null;
                if (docRequest.getDocumentFileBase64() != null && !docRequest.getDocumentFileBase64().isEmpty()) {
                    fileUrl = fileStorageService.storeBase64File(
                        docRequest.getDocumentFileBase64(),
                        docRequest.getDocumentFileName() != null ? docRequest.getDocumentFileName() : UUID.randomUUID().toString() + ".pdf"
                    );
                }
                
                DocumentVerification document = new DocumentVerification(
                    docRequest.getDocumentType(),
                    docRequest.getDocumentNumber(),
                    fileUrl
                );
                customer.addVerificationDocument(document);
            }
        }
        
        Customer savedCustomer = customerRepository.save(customer);
        return ApiResponse.success("Customer registered successfully", savedCustomer);
    }
    
    public ApiResponse getCustomerById(String customerId) {
        Optional<Customer> customerOpt = customerRepository.findByCustomerId(customerId);
        if (customerOpt.isPresent()) {
            return ApiResponse.success("Customer found", customerOpt.get());
        }
        return ApiResponse.error("Customer not found");
    }
    
    public ApiResponse getCustomerByEmail(String email) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (customerOpt.isPresent()) {
            return ApiResponse.success("Customer found", customerOpt.get());
        }
        return ApiResponse.error("Customer not found");
    }
    
    public ApiResponse getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return ApiResponse.success("All customers retrieved", customers);
    }
    
    public ApiResponse getVerifiedCustomers() {
        List<Customer> customers = customerRepository.findByVerifiedTrue();
        return ApiResponse.success("Verified customers retrieved", customers);
    }
    
    public ApiResponse getPendingVerificationCustomers() {
        List<Customer> customers = customerRepository.findByVerifiedFalse();
        return ApiResponse.success("Pending verification customers retrieved", customers);
    }
    
    public ApiResponse verifyCustomer(VerifyCustomerRequest request) {
        Optional<Customer> customerOpt = customerRepository.findByCustomerId(request.getCustomerId());
        if (customerOpt.isEmpty()) {
            return ApiResponse.error("Customer not found");
        }
        
        Customer customer = customerOpt.get();
        customer.setVerified(request.isVerified());
        customer.setVerificationStatus(request.isVerified() ? "VERIFIED" : "REJECTED");
        customer.setUpdatedAt(LocalDateTime.now());
        
        // Update verification notes on documents
        if (customer.getVerificationDocuments() != null) {
            for (DocumentVerification doc : customer.getVerificationDocuments()) {
                doc.setVerified(request.isVerified());
                doc.setVerificationNotes(request.getVerificationNotes());
            }
        }
        
        customerRepository.save(customer);
        return ApiResponse.success("Customer verification updated");
    }
    
    public ApiResponse deleteCustomer(String customerId) {
        Optional<Customer> customerOpt = customerRepository.findByCustomerId(customerId);
        if (customerOpt.isEmpty()) {
            return ApiResponse.error("Customer not found");
        }
        
        customerRepository.delete(customerOpt.get());
        return ApiResponse.success("Customer deleted successfully");
    }
    
    public Optional<Customer> findById(String customerId) {
        return customerRepository.findByCustomerId(customerId);
    }
}
