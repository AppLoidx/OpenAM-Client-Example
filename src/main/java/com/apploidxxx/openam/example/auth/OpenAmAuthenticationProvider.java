package com.apploidxxx.openam.example.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author Arthur Kupriyanov
 */
@Component
public class OpenAmAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsManager userDetailsManager;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!userDetailsManager.userExists((String) authentication.getPrincipal())) {
            userDetailsManager.createUser(new User((String) authentication.getPrincipal(),
                    (String) authentication.getCredentials(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
        }
        UserDetails userDetails = userDetailsManager.loadUserByUsername((String) authentication.getPrincipal());
        OpenAmAuthenticationToken auth = new OpenAmAuthenticationToken((String) authentication.getPrincipal(), userDetails.getAuthorities());
        auth.setAuthenticated(true);
        auth.setDetails(userDetails);
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(OpenAmAuthenticationToken.class);
    }


    @Autowired
    public void setUserDetailsService(@Qualifier("openAmUserDetailsManager") UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }
}
