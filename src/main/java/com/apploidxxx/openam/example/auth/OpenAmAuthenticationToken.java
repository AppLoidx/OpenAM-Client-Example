package com.apploidxxx.openam.example.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Arthur Kupriyanov
 */
public class OpenAmAuthenticationToken extends AbstractAuthenticationToken {
    private String username;

    OpenAmAuthenticationToken(String username, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
    }

    OpenAmAuthenticationToken(String username) {
        super(null);
        this.username = username;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return username;
    }
}
