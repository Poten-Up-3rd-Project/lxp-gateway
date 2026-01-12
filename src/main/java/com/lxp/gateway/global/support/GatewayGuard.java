package com.lxp.gateway.global.support;

import com.lxp.gateway.global.exception.GatewayErrorCode;
import com.lxp.gateway.global.exception.GatewayException;

public class GatewayGuard {

    public static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw missing(message);
        }
        return value;
    }


    private static GatewayException missing(String message) {
        return new GatewayException(GatewayErrorCode.MISSING_REQUIRED_FIELD, message);
    }
}
