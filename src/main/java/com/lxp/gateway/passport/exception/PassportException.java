package com.lxp.gateway.passport.exception;

import com.lxp.gateway.global.exception.GatewayErrorCode;
import com.lxp.gateway.global.exception.GatewayException;

public class PassportException extends GatewayException {

    public PassportException() {
        super(GatewayErrorCode.PASSPORT_GENERATE_FAIL);
    }
}
