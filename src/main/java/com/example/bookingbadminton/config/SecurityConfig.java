package com.example.bookingbadminton.config;

import com.example.bookingbadminton.constant.RoleConstant;
import com.example.bookingbadminton.security.CustomUserDetailsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableMethodSecurity
@Profile("!prod")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    final CustomUserDetailsService customUserDetailsService;

    @Value("${security.public-endpoints}")
    String[] publicEndpoints;

    @Value("${security.user-endpoints}")
    String[] userEndpoints;

    @Value("${security.admin-endpoints}")
    String[] adminEndpoints;

    @Value("${security.owner-endpoints}")
    String[] ownerEndpoints;

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                                .requestMatchers("/api/passcodes/register-owner").permitAll()
                                .requestMatchers("/api/accounts/login/owner").permitAll()
                                .requestMatchers("/api/accounts/**/unlock").hasAnyAuthority(RoleConstant.ADMIN)
                                .requestMatchers("/api/accounts/**/lock").hasAnyAuthority(RoleConstant.ADMIN)
                                .requestMatchers("/api/accounts/**/lock").hasAnyAuthority(RoleConstant.ADMIN)
                                .requestMatchers("/api/fields/**").permitAll()
                                .requestMatchers("/api/bookings/**").permitAll()
                                .requestMatchers("/api/time-slots/**").permitAll()
                                .requestMatchers(userEndpoints).hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN, RoleConstant.OWNER)
                                .requestMatchers(adminEndpoints).hasAnyAuthority(RoleConstant.ADMIN)
                                .requestMatchers(ownerEndpoints).hasAnyAuthority(RoleConstant.OWNER)
                                .requestMatchers(publicEndpoints).permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
        return httpSecurity.build();
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return new UserDetailsJwtConverter(customUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
