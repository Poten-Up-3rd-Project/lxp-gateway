package com.lxp.gateway.jwt.support;

import com.lxp.gateway.jwt.vo.TokenClaims;
import com.lxp.gateway.passport.model.Passport;
import org.springframework.stereotype.Component;

@Component
public class PassportMapper {

    public Passport toPassport(TokenClaims tokenClaims, String traceId) {
        return new Passport(
            tokenClaims.userId(), tokenClaims.authorities(), traceId
        );
    }

}
