package com.apica.journal_service.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Try to extract roles claim
        Object rolesClaim = jwt.getClaim("roles");

        if (rolesClaim instanceof List) {
            // Handle roles as a List
            @SuppressWarnings("unchecked")
            List<Object> roles = (List<Object>) rolesClaim;

            for (Object role : roles) {
                if (role instanceof Map) {
                    // Handle role objects with "authority" field
                    @SuppressWarnings("unchecked")
                    Map<String, Object> roleMap = (Map<String, Object>) role;

                    if (roleMap.containsKey("authority")) {
                        String authority = roleMap.get("authority").toString();
                        authorities.add(new SimpleGrantedAuthority(authority));
                    }
                } else {
                    // Handle simple string roles
                    String roleString = role.toString();
                    if (!roleString.startsWith("ROLE_")) {
                        roleString = "ROLE_" + roleString;
                    }
                    authorities.add(new SimpleGrantedAuthority(roleString));
                }
            }
        } else if (rolesClaim instanceof String) {
            // Handle roles as a comma-separated String
            String[] roles = ((String) rolesClaim).split(",");
            for (String role : roles) {
                String roleString = role.trim();
                if (!roleString.startsWith("ROLE_")) {
                    roleString = "ROLE_" + roleString;
                }
                authorities.add(new SimpleGrantedAuthority(roleString));
            }
        }

        return authorities;
    }}
