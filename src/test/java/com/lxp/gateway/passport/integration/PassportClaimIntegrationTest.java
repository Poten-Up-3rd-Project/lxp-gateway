package com.lxp.gateway.passport.integration;

import com.lxp.gateway.passport.component.PassportEncoder;
import com.lxp.gateway.passport.config.KeyProperties;
import com.lxp.gateway.passport.constants.PassportClaimKeys;
import com.lxp.gateway.passport.model.Passport;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Passport Claim 명세 통합 테스트
 * <p>
 * PASSPORT_CLAIM_SPECIFICATION.md에 정의된 표준을 준수하는지 검증합니다.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "passport.key.secret-key=test-secret-key-for-unit-testing-purposes-only-minimum-32-characters",
    "passport.key.duration-millis=60000"
})
@DisplayName("Passport Claim 명세 통합 테스트")
class PassportClaimIntegrationTest {

    @Autowired
    private PassportEncoder passportEncoder;

    @Autowired
    private KeyProperties keyProperties;

    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        secretKey = keyProperties.jwtSecretKey();
    }

    @Test
    @DisplayName("클레임 키가 명세와 일치해야 함")
    void testClaimKeysMatchSpecification() {
        assertEquals("uid", PassportClaimKeys.USER_ID);
        assertEquals("rol", PassportClaimKeys.ROLES);
        assertEquals("tid", PassportClaimKeys.TRACE_ID);
        assertEquals(",", PassportClaimKeys.ROLE_SEPARATOR);
    }

    @Test
    @DisplayName("userId를 uid 클레임으로 인코딩")
    void testUserIdEncoding() {
        // Given
        String userId = "user-123";
        Passport passport = new Passport(userId, List.of("ROLE_USER"), "trace-id-123");

        // When
        String encodedJwt = passportEncoder.encode(passport);
        Claims claims = parseJwt(encodedJwt);

        // Then
        assertEquals(userId, claims.get(PassportClaimKeys.USER_ID).toString());
    }

    @Test
    @DisplayName("여러 역할을 쉼표로 구분된 문자열로 인코딩")
    void testMultipleRolesEncoding() {
        // Given
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");
        Passport passport = new Passport("user-123", roles, "trace-id-123");

        // When
        String encodedJwt = passportEncoder.encode(passport);
        Claims claims = parseJwt(encodedJwt);

        // Then
        String rolesString = claims.get(PassportClaimKeys.ROLES).toString();
        assertEquals("ROLE_USER,ROLE_ADMIN", rolesString);
    }

    @Test
    @DisplayName("traceId를 tid 클레임으로 인코딩")
    void testTraceIdEncoding() {
        // Given
        String traceId = "550e8400-e29b-41d4-a716-446655440000";
        Passport passport = new Passport("user-123", List.of("ROLE_USER"), traceId);

        // When
        String encodedJwt = passportEncoder.encode(passport);
        Claims claims = parseJwt(encodedJwt);

        // Then
        assertEquals(traceId, claims.get(PassportClaimKeys.TRACE_ID).toString());
    }

    @Test
    @DisplayName("Gateway 인코딩 → 서비스 디코딩 호환성")
    void testRoundTripCompatibility() {
        // Given
        String userId = "user-789";
        List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN");
        String traceId = "550e8400-e29b-41d4-a716-446655440000";
        Passport passport = new Passport(userId, roles, traceId);

        // When - Gateway 인코딩
        String encodedJwt = passportEncoder.encode(passport);

        // When - 서비스 디코딩
        Claims claims = parseJwt(encodedJwt);
        String decodedUserId = claims.get(PassportClaimKeys.USER_ID).toString();
        List<String> decodedRoles = Arrays.asList(
            claims.get(PassportClaimKeys.ROLES).toString()
                .split(PassportClaimKeys.ROLE_SEPARATOR)
        );
        String decodedTraceId = claims.get(PassportClaimKeys.TRACE_ID).toString();

        // Then
        assertEquals(userId, decodedUserId);
        assertEquals(roles, decodedRoles);
        assertEquals(traceId, decodedTraceId);
    }

    @Test
    @DisplayName("모든 필수 클레임이 포함되어야 함")
    void testAllRequiredClaimsPresent() {
        // Given
        Passport passport = new Passport("user-123", List.of("ROLE_USER"), "trace-id-123");

        // When
        String encodedJwt = passportEncoder.encode(passport);
        Claims claims = parseJwt(encodedJwt);

        // Then
        assertNotNull(claims.get(PassportClaimKeys.USER_ID));
        assertNotNull(claims.get(PassportClaimKeys.ROLES));
        assertNotNull(claims.get(PassportClaimKeys.TRACE_ID));
        assertNotNull(claims.get("iat"));
        assertNotNull(claims.get("exp"));
    }

    private Claims parseJwt(String jwt) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(jwt)
            .getPayload();
    }
}
