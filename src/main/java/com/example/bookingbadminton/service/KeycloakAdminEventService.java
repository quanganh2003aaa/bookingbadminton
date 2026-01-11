package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.dto.request.keycloak.AdminEventPayload;
import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.User;
import com.example.bookingbadminton.repository.UserRepository;
import com.example.bookingbadminton.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminEventService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void handle(AdminEventPayload payload) {
        if (payload == null) {
            log.warn("Received null admin event payload");
            return;
        }
        if (!"USER".equalsIgnoreCase(payload.getResourceType())) {
            return; // ignore other resource types
        }

        String keycloakUserId = resolveUserId(payload);
        if (!StringUtils.hasText(keycloakUserId)) {
            log.warn("Admin event missing resource id, payload={}", payload);
            return;
        }

        if ("DELETE".equalsIgnoreCase(payload.getOperationType())) {
            handleDelete(keycloakUserId);
        } else if ("CREATE".equalsIgnoreCase(payload.getOperationType())) {
            handleCreate(keycloakUserId, payload);
        }
    }

    private String resolveUserId(AdminEventPayload payload) {
        if (StringUtils.hasText(payload.getResourceId())) {
            return payload.getResourceId();
        }
        if (StringUtils.hasText(payload.getResourcePath())) {
            String path = payload.getResourcePath();
            int lastSlash = path.lastIndexOf('/');
            if (lastSlash >= 0 && lastSlash < path.length() - 1) {
                return path.substring(lastSlash + 1);
            }
        }
        return null;
    }

    private void handleDelete(String keycloakUserId) {
        Optional<Account> accountOpt = accountRepository.findByKeycloakUserId(keycloakUserId);
        if (accountOpt.isEmpty()) {
            log.warn("No account mapped to Keycloak user id {}", keycloakUserId);
            return;
        }

        Optional<User> userOptional = userRepository.findByAccount(accountOpt.get());
        if (userOptional.isEmpty()) {
            log.warn("No user mapped to Keycloak user id {}", keycloakUserId);
            return;
        }

        User user = userOptional.get();
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        Account account = accountOpt.get();
        if (account.getDeletedAt() == null) {
            account.setDeletedAt(LocalDateTime.now());
            accountRepository.save(account);
            log.info("Soft-deleted account {} due to Keycloak admin event DELETE", account.getId());
        } else {
            log.info("Account {} already marked deleted", account.getId());
        }
    }

    private void handleCreate(String keycloakUserId, AdminEventPayload payload) {
        if (accountRepository.findByKeycloakUserId(keycloakUserId).isPresent()) {
            log.info("Account already exists for Keycloak user id {}, skip create", keycloakUserId);
            return;
        }

        var representation = toMap(payload.getRepresentation());
        String email = representation.getOrDefault("email", "").toString();
        String username = representation.getOrDefault("username", "").toString();

        String gmail = StringUtils.hasText(email) ? email : username;
        if (!StringUtils.hasText(gmail)) {
            log.warn("Cannot create account, missing email/username in representation for {}", keycloakUserId);
            return;
        }
        if (accountRepository.existsByGmailIgnoreCase(gmail)) {
            log.warn("Gmail {} already exists, skip create on admin event", gmail);
            return;
        }

        String name = representation.getOrDefault("firstName", "").toString();
        if (!StringUtils.hasText(name)) {
            name = username;
        }
        if (!StringUtils.hasText(name)) {
            name = "User " + gmail;
        }

        // Generate placeholder password and msisdn since Keycloak does not send plaintext
        String rawPassword = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        String msisdn = "0123456789";

        Account account = new Account();
        account.setGmail(gmail);
        account.setPassword(encodedPassword);
        account.setMsisdn(msisdn);
        account.setKeycloakUserId(keycloakUserId);
        accountRepository.save(account);

        User user = new User();
        user.setAccount(account);
        user.setName(name);
        user.setAvatar(com.example.bookingbadminton.constant.Const.AVATAR_DEFAULT);
        userRepository.save(user);

        log.info("Created local account/user for Keycloak user id {} email {}", keycloakUserId, gmail);
    }

    @SuppressWarnings("unchecked")
    private java.util.Map<String, Object> toMap(Object representation) {
        if (representation == null) {
            return new java.util.HashMap<>();
        }
        if (representation instanceof java.util.Map<?, ?> map) {
            return new java.util.HashMap<>((java.util.Map<String, Object>) map);
        }
        if (representation instanceof String str && StringUtils.hasText(str)) {
            try {
                return objectMapper.readValue(str, java.util.Map.class);
            } catch (Exception ignored) {
            }
        }
        return new java.util.HashMap<>();
    }
}
