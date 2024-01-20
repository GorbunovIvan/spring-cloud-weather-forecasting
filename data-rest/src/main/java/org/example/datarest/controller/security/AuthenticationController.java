package org.example.datarest.controller.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.datarest.model.security.AuthenticationRequestDto;
import org.example.datarest.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/weather/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestDto requestUser) {

        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestUser.getUsername(), requestUser.getPassword()));

            var user = userDetailsService.loadUserByUsername(requestUser.getUsername());
            String token = jwtTokenProvider.createToken(user.getUsername(), user.getAuthorities());

            var response = new HashMap<String, String>();
            response.put("username", user.getUsername());
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        var handler = new SecurityContextLogoutHandler();
        handler.logout(request, response, null);
    }
}
