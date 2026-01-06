package com.example.bookingbadminton.security;

import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.User;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-DETAILS-SERVICE")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByGmailIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("account not found"));
        User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));
        return new CustomUserDetails(user, account);
    }
}
