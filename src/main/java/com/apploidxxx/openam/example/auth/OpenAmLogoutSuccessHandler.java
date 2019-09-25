package com.apploidxxx.openam.example.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Arthur Kupriyanov
 */
@Component
public class OpenAmLogoutSuccessHandler implements LogoutSuccessHandler {
    private final String OPENAM_LOGOUT_URL = "http://openam-01.domain.com:8080/openam/XUI/#logout/";
    private final String HOME_PAGE_URL = "http://openam-01.domain.com:8888/test/home";

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        response.sendRedirect(OPENAM_LOGOUT_URL + "&goto=" + HOME_PAGE_URL);
    }
}
