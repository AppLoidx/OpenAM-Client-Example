package com.apploidxxx.openam.example.auth;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Arthur Kupriyanov
 */
@Component
public class OpenAmAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final String HOME_PAGE_URL = "http://openam-01.domain.com:8888/test/home";

    OpenAmAuthenticationFilter() {
        super("/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        Optional<Cookie> iPlanetDirectoryPro = getCookie(request);

        String redirectUrl = getRedirectURLAfterSuccessAuth(request, response);

        if (!iPlanetDirectoryPro.isPresent()) {
            response.sendRedirect(buildRedirectToLogin(redirectUrl));
            return null;
        }

        HttpEntity entity = createHttpEntityWithCookie(iPlanetDirectoryPro.get());

        ResponseEntity<OpenAmAttributeResponse> attributesResponse;
        try {
            attributesResponse = getAttributeFromOpenAMRest(entity);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return redirectTo(buildRedirectToLogin(redirectUrl), response);
            } else {
                return redirectTo(HOME_PAGE_URL, response);
            }
        }

        if (attributesResponse != null && attributesResponse.getBody() != null) {
            Optional<String> username = getUsernameByUID(attributesResponse.getBody());
            if (username.isPresent()) {
                return authenticateUser(username.get());
            }

        }

        throw new UsernameNotFoundException("Can't get username");


    }

    private Authentication authenticateUser(String user){
        OpenAmAuthenticationToken authRequest = new OpenAmAuthenticationToken(user);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private Authentication redirectTo(String redirectUrl, HttpServletResponse response) throws IOException {
        response.sendRedirect(redirectUrl);
        return null;
    }

    private ResponseEntity<OpenAmAttributeResponse> getAttributeFromOpenAMRest(HttpEntity entity){
        return new RestTemplate().exchange(OpenAmUrls.OPENAM_ATTRIBUTES_URL, HttpMethod.GET, entity, OpenAmAttributeResponse.class);
    }

    private Optional<String> getUsernameByUID(OpenAmAttributeResponse attributesResponse){
        return Arrays.stream(attributesResponse.attributes)
                .filter(a -> a.name.equals("uid"))
                .findFirst()
                .map(OpenAmAttribute::getValues)
                .map(v -> v[0]);
    }

    private Optional<Cookie> getCookie(HttpServletRequest request){
        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName()
                .equals("iPlanetDirectoryPro"))
                .findFirst();
    }
    private HttpEntity createHttpEntityWithCookie(Cookie cookie){
        HttpHeaders headers = new HttpHeaders();
        addCookieToHeaders(headers, cookie);
        return new HttpEntity(headers);
    }

    private void addCookieToHeaders(HttpHeaders headers, Cookie cookie){
        headers.add("cookie", String.format("%s=%s", cookie.getName(), cookie.getValue()));
    }

    private String getRedirectURLAfterSuccessAuth(HttpServletRequest request, HttpServletResponse response){
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        return savedRequest == null ? HOME_PAGE_URL : savedRequest.getRedirectUrl();

    }

    private String buildRedirectToLogin(String redirect){
        return OpenAmUrls.OPENAM_LOGIN_URL + "&goto=" + redirect;
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }


    @Data
    private static class OpenAmAttributeResponse {
        private OpenAmAttribute[] attributes;
    }

    @Data
    private static class OpenAmAttribute {
        private String name;
        private String[] values;
    }
}