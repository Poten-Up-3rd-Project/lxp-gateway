package com.lxp.gateway.jwt.support;

import com.lxp.gateway.global.constants.CookieConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthHeaderResolver {

    public String resolveToken(ServerHttpRequest request) {
        HttpCookie cookie = request.getCookies().getFirst(CookieConstants.ACCESS_TOKEN_NAME);

        if (cookie == null) {
            log.info("Cookies is null");
            return null;
        }

        return cookie.getValue();
    }

}
