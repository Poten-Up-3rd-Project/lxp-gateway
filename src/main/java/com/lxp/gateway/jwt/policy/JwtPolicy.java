package com.lxp.gateway.jwt.policy;

import com.lxp.gateway.jwt.vo.TokenClaims;

public interface JwtPolicy {

    /**
     * JWT 토큰의 유효성을 검증 후 복호화하여 인증 정보를 추출합니다.
     *
     * @param token 복호화할 JWT 문자열
     * @return 토큰 클레임 정보
     */
    TokenClaims verify(String token);

}
