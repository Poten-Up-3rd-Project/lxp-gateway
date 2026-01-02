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

import javax.crypto.SecretKey;
import java.util.Arrays;

@Slf4j
@Component
public class JwtPolicyImpl implements JwtPolicy {

    private final SecretKey key;

    public JwtPolicyImpl(JwtConfig jwtConfig) {
        this.key = jwtConfig.jwtSecretKey();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Signature", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    @Override
    public TokenClaims parseToken(String token) {
        Claims claims = parseClaims(token);
        return new TokenClaims(
            claims.get("userId").toString(),
            claims.getSubject(),
            Arrays.asList(claims.get("auth").toString().split(","))
        );
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // 만료 시간 계산을 위한 Claims 반환
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // 서명 오류, 토큰 형식 오류 등은 예외 처리
            throw new InvalidTokenException("Invalid JWT signature or format.");
        } catch (Exception e) {
            return null;
        }
    }
}
