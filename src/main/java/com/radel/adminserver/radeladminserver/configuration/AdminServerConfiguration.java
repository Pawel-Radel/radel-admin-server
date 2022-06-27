package com.radel.adminserver.radeladminserver.configuration;

import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;

@Configuration(proxyBeanMethods = false)
public class AdminServerConfiguration {

    private static final String ACTUATOR_ENDPOINTS_CLIENT_NAME = "actuator-client";
    private static final String ADMIN_SERVER_CONTEXT_PRINCIPAL_NAME = "admin-server-context";
    public static final String ADMIN_SERVER_INSTANCE_NAME = "admin-server";

    @Autowired
    private OAuth2AuthorizedClientManager clientManager;

    @Bean
    public HttpHeadersProvider keycloakBearerActuatorHeaderProvider() {
        return (app) -> {

            HttpHeaders httpHeaders = new HttpHeaders();

            if (!app.getRegistration().getName().equals(ADMIN_SERVER_INSTANCE_NAME)) {
                OAuth2AuthorizedClient client = clientManager.authorize(OAuth2AuthorizeRequest
                        .withClientRegistrationId(ACTUATOR_ENDPOINTS_CLIENT_NAME)
                        .principal(ADMIN_SERVER_CONTEXT_PRINCIPAL_NAME)
                        .build());

                OAuth2AccessToken accessToken = client.getAccessToken();
                String authHeader = format("%s %s", accessToken.getTokenType().getValue(), accessToken.getTokenValue());
                httpHeaders.add(HttpHeaders.AUTHORIZATION, authHeader);
            }

            return httpHeaders;
        };
    }
}
