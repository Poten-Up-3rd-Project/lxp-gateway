package com.lxp.gateway.global.exception;

import com.lxp.common.domain.exception.ErrorCode;

public enum GatewayErrorCode implements ErrorCode {

    UNAUTHORIZED_ACCESS("UNAUTHORIZED", "GATEWAY_001", "유효하지 않거나 만료된 토큰입니다."),
    PASSPORT_GENERATE_FAIL("INTERNAL_SERVER_ERROR", "GATEWAY_002", "내부 신분증 생성에 실패했습니다."),
    INTERNAL_ERROR("INTERNAL_SERVER_ERROR", "GATEWAY_003", "Internal Server Error"),
    MISSING_REQUIRED_FIELD("BAD_REQUEST", "GATEWAY_004", "필수 인증 정보(ID/PW)가 누락되었습니다."),
    ;

    private final String group;
    private final String code;
    private final String message;

    GatewayErrorCode(String group, String code, String message) {
        this.group = group;
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getGroup() {
        return this.group;
    }
}
