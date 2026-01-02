package com.lxp.gateway.jwt.adapter;

import com.lxp.gateway.jwt.policy.TokenRevocationPolicy;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
    @CircuitBreaker(name = "redis", fallbackMethod = "fallbackBlacklistCheck")
    public boolean isTokenBlacklisted(String token) {
        Boolean exists = redisTemplate.hasKey(createKey(token));
        boolean isBlacklisted = Boolean.TRUE.equals(exists);  // null-safe

        if (isBlacklisted) {
            log.info("Token is blacklisted.");
        }

        return isBlacklisted;
    }

    private boolean fallbackBlacklistCheck(String token, Throwable e) {
        log.error("Redis circuit breaker opened. Denying token access.", e);
        return true;
    }

    private String createKey(String token) {
        return TOKEN_REVOCATION_KEY + token;
    }

}

