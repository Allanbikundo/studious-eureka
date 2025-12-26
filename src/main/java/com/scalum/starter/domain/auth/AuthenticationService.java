package com.scalum.starter.domain.auth;

import com.scalum.starter.domain.notification.EmailService;
import com.scalum.starter.domain.token.TokenService;
import com.scalum.starter.domain.user.UserService;
import com.scalum.starter.dto.LoginRequest;
import com.scalum.starter.dto.SignUpRequest;
import com.scalum.starter.model.Token;
import com.scalum.starter.model.User;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthenticationService {

    private final String serverUrl;
    private final String realm;
    private final String clientId;
    private final String clientSecret;
    private final UserService userService;
    private final EmailService emailService;
    private final TokenService tokenService;

    public AuthenticationService(
            @Value("${keycloak.server-url}") String serverUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.client-id}") String clientId,
            @Value("${keycloak.client-secret}") String clientSecret,
            UserService userService,
            EmailService emailService,
            TokenService tokenService) {
        this.serverUrl = serverUrl;
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.userService = userService;
        this.emailService = emailService;
        this.tokenService = tokenService;
    }

    private Keycloak getKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

    @Transactional
    public String createUser(User user, String password, String username, String email) {
        Keycloak keycloak = getKeycloakInstance();

        try {
            UserRepresentation userRepresentation =
                    getUserRepresentation(user, password, username, email);

            Response response = keycloak.realm(realm).users().create(userRepresentation);

            if (response.getStatus() == 201) {
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                response.close();
                return userId;
            } else {
                throw new RuntimeException("Failed to create user: " + response.getStatus());
            }

        } finally {
            keycloak.close();
        }
    }

    @NotNull
    private static UserRepresentation getUserRepresentation(
            User user, String password, String username, String email) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(username);
        userRepresentation.setEmail(email);
        userRepresentation.setFirstName(user.getName());
        userRepresentation.setLastName(user.getName());

        // Set password directly during creation
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        userRepresentation.setCredentials(List.of(credential));
        return userRepresentation;
    }

    @Transactional(readOnly = true)
    public AccessTokenResponse login(LoginRequest loginRequest) {
        Keycloak userKeycloak =
                KeycloakBuilder.builder()
                        .serverUrl(serverUrl)
                        .realm(realm)
                        .grantType(OAuth2Constants.PASSWORD)
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .username(loginRequest.getUsername())
                        .password(loginRequest.getPassword())
                        .build();

        AccessTokenResponse tokenResponse = userKeycloak.tokenManager().getAccessToken();
        userKeycloak.close();

        return tokenResponse;
    }

    public AccessTokenResponse refreshToken(String refreshToken) {
        String tokenUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<AccessTokenResponse> response =
                restTemplate.postForEntity(tokenUrl, request, AccessTokenResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to refresh token: " + response.getStatusCode());
        }
    }

    @Transactional
    public void forgotPassword(String email) {
        Keycloak keycloak = getKeycloakInstance();
        try {
            List<UserRepresentation> users = keycloak.realm(realm).users().search(email, true);
            if (!users.isEmpty()) {
                UserRepresentation keycloakUser = users.get(0);
                User user = userService.findByKeycloakId(keycloakUser.getId());
                if (user != null) {
                    Token token =
                            tokenService.createToken(user, Token.TokenType.PASSWORD_RESET, 15);
                    String resetLink =
                            "http://localhost:8080/reset-password?token=" + token.getToken();
                    emailService.sendEmail(
                            keycloakUser.getEmail(),
                            "Password Reset Request",
                            "To reset your password, click the link below:\n" + resetLink);
                }
            }
        } finally {
            keycloak.close();
        }
    }

    @Transactional
    public void resetPassword(String tokenString, String newPassword) {
        Token token = tokenService.findByToken(tokenString);
        if (token != null
                && token.getUsedAt() == null
                && token.getType() == Token.TokenType.PASSWORD_RESET
                && token.getExpiresAt().isAfter(LocalDateTime.now())) {
            User user = token.getUser();
            Keycloak keycloak = getKeycloakInstance();
            try {
                UserRepresentation userRepresentation =
                        keycloak.realm(realm).users().get(user.getKeycloakId()).toRepresentation();

                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(newPassword);
                credential.setTemporary(false);

                userRepresentation.setCredentials(List.of(credential));
                keycloak.realm(realm).users().get(user.getKeycloakId()).update(userRepresentation);

                tokenService.markTokenAsUsed(token);
            } finally {
                keycloak.close();
            }
        }
    }

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        user.setEmail(signUpRequest.getEmail());
        user.setUsername(signUpRequest.getUsername());

        String keycloakId =
                createUser(
                        user,
                        signUpRequest.getPassword(),
                        signUpRequest.getUsername(),
                        signUpRequest.getEmail());
        user.setKeycloakId(keycloakId);
        userService.save(user);

        Token token = tokenService.createToken(user, Token.TokenType.EMAIL_VERIFICATION, 60);
        String verificationLink = "http://localhost:8080/verify-email?token=" + token.getToken();
        emailService.sendEmail(
                signUpRequest.getEmail(),
                "Verify Your Email",
                "To verify your email, click the link below:\n" + verificationLink);
    }

    @Transactional
    public void verifyEmail(String tokenString) {
        Token token = tokenService.findByToken(tokenString);
        if (token != null
                && token.getUsedAt() == null
                && token.getType() == Token.TokenType.EMAIL_VERIFICATION
                && token.getExpiresAt().isAfter(LocalDateTime.now())) {
            User user = token.getUser();
            Keycloak keycloak = getKeycloakInstance();
            try {
                UserRepresentation userRepresentation =
                        keycloak.realm(realm).users().get(user.getKeycloakId()).toRepresentation();
                userRepresentation.setEmailVerified(true);
                keycloak.realm(realm).users().get(user.getKeycloakId()).update(userRepresentation);
                tokenService.markTokenAsUsed(token);
                emailService.sendVerificationSuccessEmail(user.getEmail(), user.getName());
            } finally {
                keycloak.close();
            }
        }
    }
}
