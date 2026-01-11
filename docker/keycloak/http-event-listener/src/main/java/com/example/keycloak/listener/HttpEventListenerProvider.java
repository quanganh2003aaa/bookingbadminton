package com.example.keycloak.listener;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.util.JsonSerialization;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class HttpEventListenerProvider implements EventListenerProvider {
    private final URI endpoint;
    private final String authToken;
    private final HttpClient client;

    public HttpEventListenerProvider(URI endpoint, String authToken) {
        this.endpoint = endpoint;
        this.authToken = authToken;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public void onEvent(Event event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", event.getType().name());
        payload.put("realmId", event.getRealmId());
        payload.put("clientId", event.getClientId());
        payload.put("userId", event.getUserId());
        payload.put("sessionId", event.getSessionId());
        payload.put("ipAddress", event.getIpAddress());
        payload.put("details", event.getDetails());
        send(payload);
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("operationType", event.getOperationType().name());
        payload.put("realmId", event.getRealmId());
        payload.put("resourceType", event.getResourceType() != null ? event.getResourceType().name() : null);
        payload.put("resourcePath", event.getResourcePath());
        payload.put("authDetails", event.getAuthDetails());
        if (includeRepresentation) {
            payload.put("representation", event.getRepresentation());
        }
        send(payload);
    }

    private void send(Map<String, Object> body) {
        try {
            byte[] json = JsonSerialization.writeValueAsBytes(body);
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(endpoint)
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json");
            if (authToken != null && !authToken.isBlank()) {
                builder.header("X-KEYCLOAK-TOKEN", authToken);
            }
            HttpRequest request = builder.POST(HttpRequest.BodyPublishers.ofByteArray(json)).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception ignored) {
            // swallow to avoid impacting Keycloak
        }
    }

    @Override
    public void close() {
        // nothing to close
    }
}
