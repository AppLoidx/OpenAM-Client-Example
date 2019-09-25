package com.apploidxxx.openam.example.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arthur Kupriyanov
 */
@Component
@Slf4j
public class OpenAmUserDetailsManager implements UserDetailsManager {
    private List<UserDetails> userDetails = new ArrayList<>();

    @Override
    public void createUser(UserDetails user) {
        log.info("Creating user with username {}", user.getUsername());
        userDetails.add(user);
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return userDetails.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetails.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
    }
}