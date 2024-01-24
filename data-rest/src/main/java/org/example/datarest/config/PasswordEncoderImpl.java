package org.example.datarest.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderImpl extends BCryptPasswordEncoder {
    PasswordEncoderImpl() {
        super(8);
    }
}
