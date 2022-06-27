package com.radel.adminserver.radeladminserver.configuration;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import de.codecentric.boot.admin.server.config.AdminServerProperties;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final String adminUsername;

    private final AdminServerProperties adminServer;
    private final String adminPassword;

    public static final String TARGET_URL_PARAMETER = "redirectTo";

    public SecurityConfiguration(AdminServerProperties adminServer,
                                 @Value("${spring.security.user.name}") String adminUsername,
                                 @Value("${spring.security.user.password}") String adminPassword) {

        this.adminServer = adminServer;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter(TARGET_URL_PARAMETER);
        successHandler.setDefaultTargetUrl(this.adminServer.path("/"));

        http.authorizeRequests(
                        (authorizeRequests) -> authorizeRequests.antMatchers(this.adminServer.path("/assets/**")).permitAll()
                                .antMatchers(this.adminServer.path("/login")).permitAll()
                                .anyRequest().authenticated()
                ).formLogin(
                        (formLogin) -> formLogin.loginPage(this.adminServer.path("/login")).successHandler(successHandler).and()
                ).logout((logout) -> logout.logoutUrl(this.adminServer.path("/logout"))).httpBasic(Customizer.withDefaults())
                .csrf().disable();

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {

        UserDetails user = User
                .builder()
                .username(this.adminUsername)
                .password("{bcrypt}" + new BCryptPasswordEncoder().encode(this.adminPassword))
                .authorities(new ArrayList<>())
                .build();
        return new InMemoryUserDetailsManager(user);
    }

}
