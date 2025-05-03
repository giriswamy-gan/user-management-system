package com.apica.journal_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerConfig {
    @Value("${jwt.secret}")
    private String jwtSecret;

    CustomJwtAuthenticationConverter customJwtAuthenticationConverter;
    JwtAuthenticationErrorHandler jwtAuthenticationErrorHandler;

    public ResourceServerConfig(CustomJwtAuthenticationConverter customJwtAuthenticationConverter, JwtAuthenticationErrorHandler jwtAuthenticationErrorHandler) {
        this.customJwtAuthenticationConverter = customJwtAuthenticationConverter;
        this.jwtAuthenticationErrorHandler = jwtAuthenticationErrorHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/journal/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter))
                        .authenticationEntryPoint(jwtAuthenticationErrorHandler)
                        .accessDeniedHandler(jwtAuthenticationErrorHandler)
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jwtAuthenticationErrorHandler)
                        .accessDeniedHandler(jwtAuthenticationErrorHandler)
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
         return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(
             jwtSecret.getBytes(), "HmacSHA256")).build();
    }
}