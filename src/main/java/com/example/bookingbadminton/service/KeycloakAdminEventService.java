package com.example.bookingbadminton.service;

import com.example.bookingbadminton.model.dto.request.keycloak.AdminEventPayload;
import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminEventService {

    private final AccountRepository accountRepository;

    @Transactional
    public void handle(AdminEventPayload payload) {
        if (payload == null) {
            log.warn("Received null admin event payload");
            return;
        }
        if (!"USER".equalsIgnoreCase(payload.getResourceType())
                || !"DELETE".equalsIgnoreCase(payload.getOperationType())) {
            return; // ignore other events
        }

        String keycloakUserId = resolveUserId(payload);
        if (!StringUtils.hasText(keycloakUserId)) {
            log.warn("Admin event missing resource id, payload={}", payload);
            return;
        }

        Optional<Account> accountOpt = accountRepository.findByKeycloakUserId(keycloakUserId);
        if (accountOpt.isEmpty()) {
            log.warn("No account mapped to Keycloak user id {}", keycloakUserId);
            return;
        }

        Account account = accountOpt.get();
        if (account.getDeletedAt() == null) {
            account.setDeletedAt(LocalDateTime.now());
            accountRepository.save(account);
            log.info("Soft-deleted account {} due to Keycloak admin event DELETE", account.getId());
        } else {
            log.info("Account {} already marked deleted", account.getId());
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
}
