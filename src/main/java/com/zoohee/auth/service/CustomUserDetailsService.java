package com.zoohee.auth.service;

import com.zoohee.auth.dto.CustomUserDetails;
import com.zoohee.auth.entity.User;
import com.zoohee.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(username + " ::: 해당 아이디를 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(User user) {
        return new CustomUserDetails(user);
    }
}