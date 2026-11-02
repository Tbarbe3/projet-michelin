package fst_sw.michelin;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Map;

public final class TestToken {
    public static final String LUCIEN = "lucien.bramard@michelin.fr";
    public static final String NOEL = "noel.flantier@michelin.fr";
    public static final String PROF = "antoine.robert@intech.lu";

    private TestToken() {}

    public static JwtAuthenticationToken user(String username) {
        return token(username, "USER");
    }

    public static JwtAuthenticationToken admin(String username) {
        return token(username, "ADMIN");
    }

    // Reconstruit la structure que les vrais tokens : resource_access -> coursm2 -> roles
    private static JwtAuthenticationToken token(String username, String role) {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .claim("preferred_username", username)
                .claim("resource_access", Map.of("coursm2", Map.of("roles", List.of(role))))
                .build();
        return new JwtAuthenticationToken(jwt);
    }
}
