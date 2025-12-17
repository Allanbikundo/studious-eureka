package com.scalum.starter.domain.token;

import com.scalum.starter.model.Token;
import com.scalum.starter.model.User;
import com.scalum.starter.repository.TokenRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    @Transactional
    public Token createToken(User user, Token.TokenType type, int expirationMinutes) {
        String rawToken = UUID.randomUUID().toString();
        String hashedToken = hashToken(rawToken);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expirationMinutes);
        Token token = new Token(hashedToken, type, expiresAt, user);
        tokenRepository.save(token);
        // Return the raw token so it can be sent to the user
        token.setToken(rawToken);
        return token;
    }

    @Transactional(readOnly = true)
    public Token findByToken(String rawToken) {
        String hashedToken = hashToken(rawToken);
        return tokenRepository.findByToken(hashedToken).orElse(null);
    }

    @Transactional
    public void markTokenAsUsed(Token token) {
        token.setUsedAt(LocalDateTime.now());
        tokenRepository.save(token);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }
}
