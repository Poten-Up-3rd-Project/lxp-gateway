package com.lxp.gateway.jwt.policy;

public interface TokenRevocationPolicy {

    /**
     * 해당 토큰이 블랙리스트에 있는지 확인합니다.
     *
     * @param token Access Token 문자열
     * @return 블랙리스트에 있다면 true
     */
    boolean isTokenBlacklisted(String token);

}
