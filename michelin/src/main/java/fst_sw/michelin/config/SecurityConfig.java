package fst_sw.michelin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    public static boolean isAdmin(JwtAuthenticationToken auth) {
        Map<String, Object> resourceAccess = auth.getToken().getClaimAsMap("resource_access");
        if (resourceAccess == null || resourceAccess.get("coursm2") == null)
            return false;

        Map<String, Object> client = (Map<String, Object>) resourceAccess.get("coursm2");
        List<String> roles = (List<String>) client.get("roles");
        return roles != null && roles.contains("ADMIN");
    }

}