package com.geekonsite.security;

import com.geekonsite.model.Agent;
import com.geekonsite.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    private AgentRepository agentRepository;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Agent> agentOpt = agentRepository.findByAgentId(username);
        if (agentOpt.isEmpty()) {
            agentOpt = agentRepository.findByEmail(username);
        }
        
        if (agentOpt.isPresent()) {
            Agent agent = agentOpt.get();
            return new User(
                agent.getAgentId(),
                agent.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_AGENT"))
            );
        }
        
        throw new UsernameNotFoundException("User not found: " + username);
    }
}
