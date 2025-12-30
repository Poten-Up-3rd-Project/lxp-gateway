package com.lxp.gateway.jwt.adapter;

import com.lxp.gateway.jwt.policy.TokenRevocationPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisTokenRevocationAdapter implements TokenRevocationPolicy {

    private static final String TOKEN_REVOCATION_KEY = "tokenRevocation:";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean isTokenBlacklisted(String token) {
        String key = createKey(token);
        boolean result = false;

        try {
            Boolean exists = redisTemplate.hasKey(key);

            if (exists) {
                log.info("Token is blacklisted. Key: {}", key);
                result = true;
            }
        } catch (Exception e) {
            log.error("Failed to check token blacklist status in Redis. Key: {}", key, e);
        }

        return result;
    }

    private String createKey(String token) {
        return TOKEN_REVOCATION_KEY + token;
    }

    private boolean isTokenExpired(long durationSeconds) {
        return durationSeconds <= 0;
    }
}

