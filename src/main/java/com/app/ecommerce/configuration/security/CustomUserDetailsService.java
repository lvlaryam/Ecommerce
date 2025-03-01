package com.app.ecommerce.configuration.security;

import com.app.ecommerce.configuration.exception.constant.ExceptionMessages;
import com.app.ecommerce.core.user.User;
import com.app.ecommerce.core.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(ExceptionMessages.NOT_FOUND.getMessage()));

        return new UserPrincipal(user); // Wrap User in UserPrincipal
    }
}
