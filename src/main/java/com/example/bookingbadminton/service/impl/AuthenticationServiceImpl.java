package com.example.bookingbadminton.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.bookingbadminton.config.keycloak.KeycloakProperties;
import com.example.bookingbadminton.constant.CommonConstant;
import com.example.bookingbadminton.constant.Const;
import com.example.bookingbadminton.constant.ErrorMessage;
import com.example.bookingbadminton.exception.InvalidDataException;
import com.example.bookingbadminton.exception.KeycloakException;
import com.example.bookingbadminton.exception.ResourceNotFoundException;
import com.example.bookingbadminton.mapper.AuthMapper;
import com.example.bookingbadminton.model.Enum.ActiveStatus;
import com.example.bookingbadminton.model.Enum.RegisterStatus;
import com.example.bookingbadminton.model.Enum.TypePasscode;
import com.example.bookingbadminton.model.dto.request.auth.*;
import com.example.bookingbadminton.model.dto.request.user.UserResponseDto;
import com.example.bookingbadminton.model.dto.response.auth.AccountResponseDto;
import com.example.bookingbadminton.model.dto.response.auth.LoginResponseDto;
import com.example.bookingbadminton.model.dto.response.auth.RefreshTokenResponseDto;
import com.example.bookingbadminton.model.entity.*;
import com.example.bookingbadminton.payload.RegisterOwnerRequest;
import com.example.bookingbadminton.payload.RegisterOwnerResponse;
import com.example.bookingbadminton.payload.request.RegisterUserRequest;
import com.example.bookingbadminton.repository.*;
import com.example.bookingbadminton.service.AuthenticationService;
import com.example.bookingbadminton.service.EmailService;
import com.example.bookingbadminton.util.OtpUtil;
import com.example.bookingbadminton.util.UploadFileUtil;
import com.example.bookingbadminton.util.keycloak.KeycloakUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.bookingbadminton.constant.CommonConstant.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final KeycloakProperties keycloakProperties;
    private final RestTemplate restTemplate;
    private final KeycloakUtil keycloakUtil;
    private final EmailService emailService;
    private final PasscodeRepository passcodeRepository;
    private final UploadFileUtil uploadFileUtil;
    private final AuthMapper authMapper;
    private final OtpUtil otpUtil;
    private final RegisterOwnerRepository registerOwnerRepository;
    private final OwnerRepository ownerRepository;
    private final AdminRepository adminRepository;
    private ConcurrentHashMap<String, PendingRegistrationRequestDto> pendingRegistrationRequestMap = new ConcurrentHashMap<>();
    private Map<String, PendingResetPasswordRequestDto> pendingResetPasswordMap = new ConcurrentHashMap<>();


    @Override
    @Transactional
    public LoginResponseDto authentication(LoginRequestDto request) {
        Account account = accountRepository.findByGmailIgnoreCase(request.getUsername())
                .orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "Thông tin tài khoản không chính xác."));
        User user = userRepository.findByAccount(account)
                .orElse(null);
        Owner owner = ownerRepository.findByAccount(account)
                .orElse(null);
        Admin admin = adminRepository.findByAccount(account)
                .orElse(null);

        if (user == null && owner == null && admin == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Thông tin tài khoản không chính xác.");
        }

        UUID accountId;
        if (user != null){
            accountId = user.getId();
        } else if (owner != null){
            accountId = owner.getId();
        } else {
            accountId = admin.getId();
        }

        final String url = keycloakProperties.serverUrl() + "realms/" + keycloakProperties.realm() + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", keycloakProperties.clientId());
        params.add("client_secret", keycloakProperties.clientSecret());
        params.add("scope", "openid");
        params.add("username", request.getUsername());
        params.add("password", request.getPassword());

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers);
        try {
            var response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = response.getBody();

                String accessToken = (String) body.get("access_token");
                DecodedJWT decodedJWT = JWT.decode(accessToken);

                Map<String, Claim> claims = decodedJWT.getClaims();

                List<String> realmRoles = (List<String>) claims.get("realm_access")
                        .asMap().get("roles");

                String role = realmRoles.toString().contains("ADMIN") ? "ADMIN" : realmRoles.toString().contains("OWNER") ? "OWNER" : "USER";

                return LoginResponseDto.builder()
                        .tokenType(CommonConstant.BEARER_TOKEN)
                        .userId(String.valueOf(accountId))
                        .keycloakUserId(keycloakUtil.getUserId(request.getUsername()))
                        .role(role)
                        .accessToken(accessToken)
                        .refreshToken((String) body.get(REFRESH_TOKEN))
                        .build();
            }
            else {
                log.error("Đăng nhập thất bại với username = {}", request.getUsername());
            }
        } catch (Exception ex) {
            throw new KeycloakException(ErrorMessage.Auth.ERR_LOGIN_FAILED_IN_KEYCLOAK);
        }
        throw new InvalidDataException(ErrorMessage.Auth.ERR_USERNAME_PASSWORD_INCORRECT);
    }

    @Override
    public String lock(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng!"));
        Account account = user.getAccount();
        if (account.getDeletedAt() == null) {
            account.setUpdatedAt(LocalDateTime.now());
            account.setDeletedAt(LocalDateTime.now());
            accountRepository.save(account);
        }
        return "Khóa tài khoản thành công";
    }

    @Override
    public String unlock(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng!"));
        Account account = user.getAccount();
        if (account.getDeletedAt() != null) {
            account.setUpdatedAt(LocalDateTime.now());
            account.setDeletedAt(null);
            accountRepository.save(account);
        }
        return "Mở khóa tài khoản thành công";
    }

    private Account checkAccountLogin(String gmail, String password){
        Account account = accountRepository.findByGmailIgnoreCase(gmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thông tin đăng nhập không chính xác!"));
        if (account.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thông tin đăng nhập không chính xác!");
        }
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thông tin đăng nhập không chính xác!");
        }
        return account;
    }

    @Override
    @Transactional
    public void registerOwner(RegisterOwnerRequest request) {

        final String url = keycloakProperties.serverUrl() + "admin/realms/" + keycloakProperties.realm() + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, CommonConstant.BEARER_TOKEN + " " + keycloakUtil.getAdminToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> user = new HashMap<>();
        user.put("username", request.gmail());
        user.put("enabled", true);
        user.put("email", request.gmail());
        user.put("emailVerified", false);
        user.put("firstName", request.nameOwner());
        user.put("lastName", request.nameOwner());
        user.put("credentials", List.of(Map.of(
                "type", "password",
                "value", request.password(),
                "temporary", false
        )));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(user, headers);
        String userId = null;
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().isError()) {
                String errorBody = response.getBody();

                if (response.getStatusCode() == HttpStatus.CONFLICT && errorBody != null && !errorBody.isEmpty()) {
                    throw new KeycloakException(errorBody);
                }
                throw new KeycloakException(ErrorMessage.Auth.ERR_CAN_NOT_CREATE_USER);
            }

            userId = keycloakUtil.getUserId(request.gmail());

            String roleId;

            roleId = keycloakUtil.getRoleId("OWNER");
            keycloakUtil.assignRoleToUser(userId, roleId, "OWNER");


        } catch (Exception ex) {
            throw new KeycloakException(ex.getMessage());
        }

        String otp = OtpUtil.generateOtp();

        if (accountRepository.existsByGmailIgnoreCase(request.gmail())) {
            throw new InvalidDataException("Email đã  tồn tại. Vui lòng tạo bằng email khác");
        }

        Account account  = new Account();
        account.setGmail(request.gmail());
        account.setPassword(passwordEncoder.encode(request.password()));
        account.setMsisdn(request.mobileContact());
        account.setKeycloakUserId(userId);

        accountRepository.save(account);

        Passcode passcode = new Passcode();
        passcode.setAccount(account);
        passcode.setCode(otp);
        passcode.setTime(LocalDateTime.now().plusMinutes(5));
        passcode.setActive(ActiveStatus.ACTIVE);
        passcode.setTotalDay(passcode.getTotalDay() + 1);
        passcode.setTotalMonth(passcode.getTotalMonth() + 1);
        passcode.setType(TypePasscode.REGISTER_OWNER_CODE);
        passcodeRepository.save(passcode);

        PendingRegistrationRequestDto pending = new PendingRegistrationRequestDto();

        pending.setRequest(request);
        pending.setPasscode(passcode);

        pendingRegistrationRequestMap.put(request.gmail(), pending);

        emailService.sendRegistrationOtpByEmail(request.gmail(), request.nameOwner(), otp);
    }

    @Override
    @Transactional
    public RegisterOwnerResponse verifyOtpToRegister(VerifyOtpRequestDto request, MultipartFile file) {
        PendingRegistrationRequestDto pending = pendingRegistrationRequestMap.get(request.getEmail());

        if (pending == null){
            throw new InvalidDataException(ErrorMessage.Auth.ERR_PENDING_REGISTER_REQUEST_NULL);
        }

        if (pending.getPasscode().getActive() != ActiveStatus.ACTIVE) {
            throw new InvalidDataException("Passcode ko hoạt động");
        }

        if (pending.isExpired())
            throw new InvalidDataException(ErrorMessage.Auth.ERR_OTP_EXPIRED);

        if (!pending.getPasscode().getCode().equals(request.getOtp()))
            throw new InvalidDataException(ErrorMessage.Auth.ERR_OTP_NOT_MATCH);
        pending.setFile(file);
        if (pending.getFile() == null) {
            throw new InvalidDataException("File QR is required");
        }

        if (!keycloakUtil.verifyEmail(keycloakUtil.getUserId(request.getEmail()), true)) {
            throw new KeycloakException(ErrorMessage.Auth.ERR_VERIFY_FAILED_IN_KEYCLOAK);
        }

        String imageQrSecure = uploadFileUtil.uploadFile(pending.getFile());

        RegisterOwnerRequest req = pending.getRequest();

        RegisterOwner registerOwner = new RegisterOwner();
        registerOwner.setGmail(req.gmail());
        registerOwner.setActive(RegisterStatus.PENDING);
        registerOwner.setAccount(pending.getPasscode().getAccount());
        registerOwner.setAddress(req.address());
        registerOwner.setName(req.nameOwner());
        registerOwner.setImgQr(imageQrSecure);
        registerOwner.setLinkMap(req.linkMap());
        registerOwner.setMobileContact(req.mobileContact());

        pending.getPasscode().setActive(ActiveStatus.INACTIVE);

        passcodeRepository.save(pending.getPasscode());

        registerOwnerRepository.save(registerOwner);

        pendingRegistrationRequestMap.remove(request.getEmail());

        return authMapper.mapToResponse(registerOwner);
    }

    @Override
    public void logout(LogoutRequestDto logoutRequestDto) {
        String url = keycloakProperties.serverUrl()
                + "/realms/" + keycloakProperties.realm()
                + "/protocol/openid-connect/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(CLIENT_ID, keycloakProperties.clientId());
        body.add(CLIENT_SECRET, keycloakProperties.clientSecret());
        body.add(REFRESH_TOKEN, logoutRequestDto.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Logout failed: {}", response.getBody());
                throw new KeycloakException("Failed to logout");
            }

            log.info("Logout successfully from Keycloak");

        } catch (Exception ex) {
            throw new KeycloakException("Error calling Keycloak logout: " + ex.getMessage());
        }
    }

    @Override
    public void forgotPassword(ForgotPasswordRequestDto request) {
        Passcode passcode = otpUtil.hasValidBeforeHasOtp(request.getEmail());

        PendingResetPasswordRequestDto pending = new PendingResetPasswordRequestDto();

        pending.setRequest(request);
        pending.setPasscode(passcode);

        pendingResetPasswordMap.put(request.getEmail(), pending);

        emailService.sendForgotPasswordOtpByEmail(request.getEmail(), request.getEmail(), passcode.getCode());
    }

    @Override
    public boolean verifyOtpToResetPassword(VerifyOtpRequestDto request) {
        PendingResetPasswordRequestDto pending = pendingResetPasswordMap.get(request.getEmail());

        if (pending == null)
            throw new InvalidDataException(ErrorMessage.Auth.ERR_PENDING_RESET_REQUEST_NULL);

        if (pending.isExpired())
            throw new InvalidDataException(ErrorMessage.Auth.ERR_OTP_EXPIRED);

        otpUtil.hasValidAfterHasOtp(pending.getPasscode(), request.getOtp());

        return pendingResetPasswordMap.containsKey(request.getEmail())
                && pendingResetPasswordMap.get(request.getEmail()).getPasscode().getCode().equals(request.getOtp());
    }

    @Override
    public AccountResponseDto resetPassword(ResetPasswordRequestDto request) {
        PendingResetPasswordRequestDto pending = pendingResetPasswordMap.get(request.getEmail());
        if (pending == null)
            throw new InvalidDataException(ErrorMessage.Auth.ERR_PENDING_RESET_REQUEST_NULL);

        Account account = accountRepository.findByGmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        PasswordEncoder encoder = new BCryptPasswordEncoder(10);

        if (encoder.matches(request.getNewPassword(), account.getPassword()))
            throw new InvalidDataException(ErrorMessage.User.ERR_DUPLICATE_OLD_PASSWORD);

        String userId = keycloakUtil.getUserId(request.getEmail());

        boolean kcReset = keycloakUtil.resetPassword(userId, request.getNewPassword());
        if (!kcReset)
            throw new KeycloakException(ErrorMessage.Auth.ERR_CAN_NOT_SEND_RESET_PASSWORD_EMAIL);

        account.setPassword(encoder.encode(request.getNewPassword()));
        accountRepository.save(account);

        pendingResetPasswordMap.remove(request.getEmail());

        return AccountResponseDto.builder()
                .gmail(account.getGmail())
                .msisdn(account.getMsisdn())
                .password(account.getPassword())
                .build();
    }

    @Override
    public RefreshTokenResponseDto refresh(RefreshTokenRequestDto request) {
        final String url = keycloakProperties.serverUrl() + "/realms/" + keycloakProperties.realm() + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add(CLIENT_ID, keycloakProperties.clientId());
        params.add(CLIENT_SECRET, keycloakProperties.clientSecret());
        params.add("grant_type", REFRESH_TOKEN);
        params.add(REFRESH_TOKEN, request.getRefreshToken());

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers);

        try {
            var response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<Object, Object> body = response.getBody();
                String accessToken = (String) body.get("access_token");

                return RefreshTokenResponseDto.builder()
                        .tokenType(CommonConstant.BEARER_TOKEN)
                        .accessToken(accessToken)
                        .refreshToken((String) body.get("refresh_token"))
                        .build();
            }
        } catch (Exception ex) {
            throw new KeycloakException(ErrorMessage.Auth.ERR_LOGIN_FAILED_IN_KEYCLOAK);
        }
        throw new InvalidDataException(ErrorMessage.Auth.ERR_USERNAME_PASSWORD_INCORRECT);
    }

    @Override
    public UserResponseDto registerUser(RegisterUserRequest request) {
        final String url = keycloakProperties.serverUrl() + "admin/realms/" + keycloakProperties.realm() + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, CommonConstant.BEARER_TOKEN + " " + keycloakUtil.getAdminToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> userK = new HashMap<>();
        userK.put("username", request.account().gmail());
        userK.put("enabled", true);
        userK.put("email", request.account().gmail());
        userK.put("emailVerified", true);
        userK.put("firstName", request.name());
        userK.put("lastName", request.name());
        userK.put("credentials", List.of(Map.of(
                "type", "password",
                "value", request.account().password(),
                "temporary", false
        )));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userK, headers);
        String userId = null;
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().isError()) {
                String errorBody = response.getBody();

                if (response.getStatusCode() == HttpStatus.CONFLICT && errorBody != null && !errorBody.isEmpty()) {
                    throw new KeycloakException(errorBody);
                }
                throw new KeycloakException(ErrorMessage.Auth.ERR_CAN_NOT_CREATE_USER);
            }

            userId = keycloakUtil.getUserId(request.account().gmail());

            String roleId;
            userId = keycloakUtil.getUserId(request.account().gmail());

            roleId = keycloakUtil.getRoleId("USER");
            keycloakUtil.assignRoleToUser(userId, roleId, "USER");


        } catch (Exception ex) {
            throw new KeycloakException(ex.getMessage());
        }

        if (accountRepository.existsByGmailIgnoreCase(request.account().gmail())) {
            throw new InvalidDataException("Email đã  tồn tại. Vui lòng tạo bằng email khác");
        }

        Account account  = new Account();
        account.setGmail(request.account().gmail());
        account.setPassword(passwordEncoder.encode(request.account().password()));
        account.setMsisdn(request.account().msisdn());
        account.setKeycloakUserId(userId);

        accountRepository.save(account);

        User user = new User();
        user.setAccount(account);
        user.setAvatar(Const.AVATAR_DEFAULT);
        user.setName(request.name());
        userRepository.save(user);

        return UserResponseDto.builder().id(user.getId()).name(user.getName()).avatar(user.getAvatar()).build();
    }
}
