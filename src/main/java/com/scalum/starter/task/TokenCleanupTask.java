package com.scalum.starter.task;

import com.scalum.starter.repository.TokenRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TokenCleanupTask {

    private final TokenRepository tokenRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Run every day at midnight
    @Transactional
    public void deleteOldTokens() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(3);
        tokenRepository.deleteOldTokens(cutoff);
    }
}
