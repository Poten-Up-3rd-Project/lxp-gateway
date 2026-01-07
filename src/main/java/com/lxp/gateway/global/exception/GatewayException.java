package com.lxp.gateway.global.exception;

import com.lxp.common.domain.exception.DomainException;

public class GatewayException extends DomainException {

    public GatewayException(GatewayErrorCode errorCode) {
        super(errorCode);
    }

    public GatewayException(GatewayErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
