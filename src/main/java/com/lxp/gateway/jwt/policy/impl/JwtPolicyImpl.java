package com.lxp.gateway.jwt.policy.impl;

import com.lxp.gateway.jwt.config.JwtConfig;
import com.lxp.gateway.jwt.exception.InvalidTokenException;
import com.lxp.gateway.jwt.policy.JwtPolicy;
import com.lxp.gateway.jwt.vo.TokenClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Arrays;

@Slf4j
@Component
public class JwtPolicyImpl implements JwtPolicy {

    private final PublicKey key;

    public JwtPolicyImpl(JwtConfig jwtConfig) throws Exception {
        this.key = jwtConfig.getPublicKey();
    }

    @Override
    public TokenClaims verify(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            return new TokenClaims(
                claims.get("userId").toString(),
                claims.getSubject(),
                Arrays.asList(claims.get("auth").toString().split(","))
            );

        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token");
            throw new InvalidTokenException("Expired JWT token");

        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT signature or format");
            throw new InvalidTokenException("Invalid JWT token");

        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token");
            throw new InvalidTokenException("Unsupported JWT token");

        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty");
            throw new InvalidTokenException("Invalid JWT token");
        }
    }

}
