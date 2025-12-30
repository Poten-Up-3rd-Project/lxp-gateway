package com.lxp.gateway.jwt.adapter;

import com.lxp.gateway.global.constants.MDCConstants;
import com.lxp.gateway.jwt.vo.TokenClaims;
import com.lxp.gateway.passport.model.Passport;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class PassportMapper {

    public Passport toPassport(TokenClaims tokenClaims) {
        return new Passport(
            tokenClaims.userId(), tokenClaims.authorities(), MDC.get(MDCConstants.TRACE_ID)
        );
    }

}
