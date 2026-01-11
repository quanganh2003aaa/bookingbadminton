package com.example.bookingbadminton.security;

import com.example.bookingbadminton.constant.RoleConstant;
import com.example.bookingbadminton.model.entity.Account;
import com.example.bookingbadminton.model.entity.Admin;
import com.example.bookingbadminton.model.entity.Owner;
import com.example.bookingbadminton.model.entity.User;
import com.example.bookingbadminton.repository.AccountRepository;
import com.example.bookingbadminton.repository.AdminRepository;
import com.example.bookingbadminton.repository.OwnerRepository;
import com.example.bookingbadminton.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-DETAILS-SERVICE")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByGmailIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("account not found"));
        // Owner/Admin account might not have a User profile; allow missing User but keep account for authN
        User user = userRepository.findByAccount(account).orElse(null);
        Owner owner = ownerRepository.findByAccount(account).orElse(null);
        Admin admin = adminRepository.findByAccount(account).orElse(null);

        Set<String> roles = new HashSet<>();
        if (user != null) {
            roles.add(RoleConstant.USER);
        }
        if (owner != null) {
            roles.add(RoleConstant.OWNER);
        }
        if (admin != null) {
            roles.add(RoleConstant.ADMIN);
        }
        if (roles.isEmpty()) {
            roles.add(RoleConstant.USER); // fallback to basic role
        }

        return new CustomUserDetails(user, account, roles);
    }
}
