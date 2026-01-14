package com.lxp.gateway.passport.component;

import com.lxp.gateway.passport.config.KeyProperties;
import com.lxp.gateway.passport.constants.PassportClaimKeys;
import com.lxp.gateway.passport.model.Passport;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class PassportEncoder {

    private final SecretKey key;
    private final int duration;

    public PassportEncoder(KeyProperties properties) throws Exception {
        this.key = properties.jwtSecretKey();
        this.duration = properties.getDurationMillis();
    }

    /**
     * Passport 객체를 JWT 문자열로 변환합니다.
     * 
     * 클레임 명세: PASSPORT_CLAIM_SPECIFICATION.md 참고
     * - uid: 사용자 ID
     * - rol: 역할 (쉼표로 구분된 문자열)
     * - tid: 분산 추적 ID
     *
     * @param passport Passport 객체
     * @return 서명된 Passport JWT 문자열
     */
    public String encode(Passport passport) {
        // 역할을 쉼표로 구분된 문자열로 변환
        String rolesString = String.join(PassportClaimKeys.ROLE_SEPARATOR, passport.role());
        
        Claims claims = Jwts.claims()
            .add(PassportClaimKeys.USER_ID, passport.userId())
            .add(PassportClaimKeys.ROLES, rolesString)
            .add(PassportClaimKeys.TRACE_ID, passport.traceId())
            .build();

        return Jwts.builder()
            .claims(claims)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + duration))
            .signWith(key)
            .compact();
    }

}
