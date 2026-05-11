-- GeekOnSites MySQL Database Schema

-- Create Database
CREATE DATABASE IF NOT EXISTS geekonsites_db;
USE geekonsites_db;

-- Customers Table
CREATE TABLE IF NOT EXISTS customers (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Agents Table
CREATE TABLE IF NOT EXISTS agents (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    specialization VARCHAR(100),
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Support Tickets Table
CREATE TABLE IF NOT EXISTS support_tickets (
    id VARCHAR(36) PRIMARY KEY,
    customer_id VARCHAR(36),
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50),
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
    status ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') DEFAULT 'OPEN',
    agent_id VARCHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (agent_id) REFERENCES agents(id)
);

-- Call Sessions Table
CREATE TABLE IF NOT EXISTS call_sessions (
    id VARCHAR(36) PRIMARY KEY,
    ticket_id VARCHAR(36),
    agent_id VARCHAR(36),
    customer_id VARCHAR(36),
    status ENUM('INITIATED', 'ACTIVE', 'COMPLETED', 'FAILED') DEFAULT 'INITIATED',
    support_level ENUM('L1', 'L2', 'L3') DEFAULT 'L1',
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES support_tickets(id),
    FOREIGN KEY (agent_id) REFERENCES agents(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- IVR Options Table
CREATE TABLE IF NOT EXISTS ivr_options (
    id VARCHAR(36) PRIMARY KEY,
    issue_category VARCHAR(100) NOT NULL,
    description TEXT,
    next_step VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Document Verification Table
CREATE TABLE IF NOT EXISTS document_verifications (
    id VARCHAR(36) PRIMARY KEY,
    customer_id VARCHAR(36),
    document_type VARCHAR(50),
    file_path VARCHAR(255),
    verification_status ENUM('PENDING', 'VERIFIED', 'REJECTED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Insert Sample Data
INSERT INTO customers (id, name, email, password, phone) VALUES 
('cust_001', 'John Doe', 'john@example.com', '$2a$10$YourHashedPasswordHere', '+1234567890'),
('cust_002', 'Jane Smith', 'jane@example.com', '$2a$10$YourHashedPasswordHere', '+0987654321');

INSERT INTO agents (id, name, email, password, phone, specialization) VALUES 
('agent_001', 'Tech Support 1', 'tech1@example.com', '$2a$10$YourHashedPasswordHere', '+1111111111', 'Hardware'),
('agent_002', 'Tech Support 2', 'tech2@example.com', '$2a$10$YourHashedPasswordHere', '+2222222222', 'Software');

INSERT INTO ivr_options (id, issue_category, description, next_step) VALUES 
('ivr_001', 'Hardware Issue', 'Problems with computer hardware', 'Connect to hardware specialist'),
('ivr_002', 'Software Issue', 'Problems with software applications', 'Connect to software specialist'),
('ivr_003', 'Network Issue', 'Internet connectivity problems', 'Connect to network specialist');
