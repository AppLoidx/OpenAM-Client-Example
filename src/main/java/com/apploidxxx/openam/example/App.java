package com.apploidxxx.openam.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * @author Arthur Kupriyanov
 */
@EnableWebSecurity
@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class App extends WebSecurityConfigurerAdapter {

    public App(AbstractAuthenticationProcessingFilter authenticationProcessingFilter, LogoutSuccessHandler logoutSuccessHandler) {
        this.authenticationProcessingFilter = authenticationProcessingFilter;
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


    private final AbstractAuthenticationProcessingFilter authenticationProcessingFilter;

    private final LogoutSuccessHandler logoutSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .addFilterAt(authenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/login", "/logout", "/test/home").permitAll()
                .antMatchers("/test/hello").hasAnyAuthority("ROLE_USER")
                .and().formLogin().loginPage("/login")
                .and().logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler);
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
