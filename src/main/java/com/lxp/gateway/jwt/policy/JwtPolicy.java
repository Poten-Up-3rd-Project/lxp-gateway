package com.lxp.gateway.jwt.policy;

import com.lxp.gateway.jwt.vo.TokenClaims;

public interface JwtPolicy {

    /**
     * 토큰의 유효성을 검증합니다.
     *
     * @param token 검증할 JWT 문자열
     * @return 유효성 여부
     */
    boolean validateToken(String token);

    /**
     * JWT 토큰을 복호화하여 인증 정보를 추출합니다.
     *
     * @param token 복호화할 JWT 문자열
     * @return 토큰 클레임 정보
     */
    TokenClaims parseToken(String token);

}
