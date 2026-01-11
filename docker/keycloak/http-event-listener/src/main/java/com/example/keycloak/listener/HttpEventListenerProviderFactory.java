package com.example.keycloak.listener;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.net.URI;

public class HttpEventListenerProviderFactory implements EventListenerProviderFactory {
    private static final String ID = "http";
    private URI endpoint;
    private String authToken;

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new HttpEventListenerProvider(endpoint, authToken);
    }

    @Override
    public void init(Config.Scope config) {
        String endpointStr = config.get("endpoint");
        if (endpointStr == null || endpointStr.isBlank()) {
            throw new IllegalStateException("Missing required config: endpoint");
        }
        this.endpoint = URI.create(endpointStr);
        this.authToken = config.get("auth-token");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // no-op
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public String getId() {
        return ID;
    }
}
