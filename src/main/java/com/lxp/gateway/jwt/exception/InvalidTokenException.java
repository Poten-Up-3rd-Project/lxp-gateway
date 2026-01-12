package com.lxp.gateway.jwt.exception;

import com.lxp.gateway.global.exception.GatewayErrorCode;
import com.lxp.gateway.global.exception.GatewayException;

public class InvalidTokenException extends GatewayException {

    public InvalidTokenException(String message) {
        super(GatewayErrorCode.UNAUTHORIZED_ACCESS, message);
    }

}
