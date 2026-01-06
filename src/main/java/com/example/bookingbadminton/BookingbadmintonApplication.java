package com.example.bookingbadminton;

import com.example.bookingbadminton.config.keycloak.KeycloakProperties;
import com.example.bookingbadminton.config.properties.AdminInfoProperties;
import com.example.bookingbadminton.config.properties.FileUploadProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AdminInfoProperties.class, KeycloakProperties.class, FileUploadProperties.class })
public class BookingbadmintonApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingbadmintonApplication.class, args);
	}

}
