package com.titan.dispatch.infrastructure.security;

import com.titan.dispatch.domain.entity.SystemUser;
import com.titan.dispatch.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SystemUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new User(
                user.getId().toString(), // Principal becomes the UUID
                user.getPasswordHash(),
                user.getIsActive(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getAuthority()))
        );
    }
}