package com.example.bookingbadminton;

import com.example.bookingbadminton.config.keycloak.KeycloakProperties;
import com.example.bookingbadminton.config.properties.AdminInfoProperties;
import com.example.bookingbadminton.config.properties.FileUploadProperties;
import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.Admin;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@Log4j2
@RequiredArgsConstructor
@SpringBootApplication(scanBasePackages = "com.example.bookingbadminton")
@EnableConfigurationProperties({AdminInfoProperties.class, KeycloakProperties.class, FileUploadProperties.class })
public class BookingbadmintonApplication {

    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;

    public static void main(String[] args) {
        Environment env = SpringApplication.run(BookingbadmintonApplication.class, args).getEnvironment();
        String appName = env.getProperty("spring.application.name");
        if (appName != null) {
            appName = appName.toUpperCase();
        }
        String port = env.getProperty("server.port");
        log.info("-------------------------START {} Application------------------------------", appName);
        log.info("   Application         : {}", appName);
        log.info("   Url swagger-ui      : http://localhost:{}/swagger-ui.html", port);
        log.info("-------------------------START SUCCESS {} Application------------------------------", appName);
    }

    @Bean
    CommandLineRunner init(AdminInfoProperties adminInfo) {
        return args -> {
            if(adminRepository.count() == 0) {
                Account account = Account.builder()
                        .gmail(adminInfo.getEmail())
                        .password(adminInfo.getPassword())
                        .msisdn(adminInfo.getPhone())
                        .build();

                accountRepository.save(account);

                Admin admin = Admin.builder()
                        .account(account)
                        .name(adminInfo.getFirstName())
                        .build();
                adminRepository.save(admin);

                log.info("admin created successful with name: {} and password = {}", account.getGmail(), account.getPassword());
            }
        };
    }
}
