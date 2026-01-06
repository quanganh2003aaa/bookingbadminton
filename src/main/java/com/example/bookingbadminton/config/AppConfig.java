package com.example.bookingbadminton.config;

import com.sendgrid.SendGrid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "APP-CONFIG")
public class AppConfig implements WebMvcConfigurer {

    private final MultipartJacksonHttpMessageConverter multipartJacksonHttpMessageConverter;
    @Value("${spring.sendGrid.apiKey}")
    private String apiKey;

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(multipartJacksonHttpMessageConverter);
    }

    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(apiKey);
    }

}
