package org.example.datarest.service.security;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.datarest.model.security.Role;
import org.example.datarest.model.security.User;
import org.example.datarest.repository.security.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.default-user.username}")
    private String defaultUserUsername;

    @Value("${security.default-user.password}")
    private String defaultUserPassword;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username: '" + username + "'"));
    }

    @PostConstruct
    private void init() {
        checkForDefaultUser();
    }

    private void checkForDefaultUser() {

        // Presence check
        var defaultUser = userRepository.findByUsername(defaultUserUsername);
        if (defaultUser.isPresent()) {
            return;
        }

        // Creating a user
        var user = new User();
        user.setUsername(defaultUserUsername);
        user.setPassword(passwordEncoder.encode(defaultUserPassword));
        user.setRole(Role.USER);
        user.setIsActive(true);

        userRepository.save(user);
    }
}
