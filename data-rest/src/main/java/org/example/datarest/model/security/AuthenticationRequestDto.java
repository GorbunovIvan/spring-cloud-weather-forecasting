package org.example.datarest.model.security;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "username")
@ToString
public class AuthenticationRequestDto {
    private String username;
    private String password;
}
