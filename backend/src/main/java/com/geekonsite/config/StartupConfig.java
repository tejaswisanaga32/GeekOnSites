package com.geekonsite.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(1)
public class StartupConfig implements CommandLineRunner {
    
    @Override
    public void run(String... args) {
        log.info("Initializing GeekOnSite Call Management System...");
        log.info("System ready at http://localhost:8080");
        log.info("API Documentation:");
        log.info("  - Public endpoints: /api/public/**");
        log.info("  - Auth endpoints: /api/auth/**");
        log.info("  - Agent endpoints: /api/agent/**");
        log.info("  - Customer endpoints: /api/customers/**");
        log.info("  - Call endpoints: /api/calls/**");
    }
}
