package com.lxp.gateway.passport.constants;

/**
 * Passport JWT Claim Keys
 * 
 * Gateway와 모든 마이크로서비스에서 사용하는 표준 클레임 키 정의입니다.
 * 이 상수들은 반드시 Passport JWT를 인코딩/디코딩할 때 사용되어야 합니다.
 * 
 * 자세한 명세는 PASSPORT_CLAIM_SPECIFICATION.md를 참고하세요.
 */
public interface PassportClaimKeys {
    
    // Passport Custom Claims
    
    /**
     * 사용자 ID 클레임 키
     * 
     * 사용자의 고유한 ID를 저장합니다.
     * 
     * Example: "uid": "user-123"
     */
    String USER_ID = "uid";
    
    /**
     * 사용자 역할(권한) 클레임 키
     * 
     * 사용자의 역할을 쉼표로 구분된 문자열로 저장합니다.
     * 절대로 배열이나 리스트 형태로 저장하면 안 됩니다.
     * 
     * Example: "rol": "ROLE_USER,ROLE_ADMIN"
     */
    String ROLES = "rol";
    
    /**
     * 분산 추적 ID 클레임 키
     * 
     * 요청의 분산 추적을 위한 고유 ID입니다.
     * UUID 형식이어야 합니다.
     * 
     * Example: "tid": "550e8400-e29b-41d4-a716-446655440000"
     */
    String TRACE_ID = "tid";
    
    // Role Separator
    
    /**
     * 역할 구분자
     * 
     * 여러 역할을 문자열로 저장할 때 사용하는 구분자입니다.
     * 
     * Example: "ROLE_USER,ROLE_ADMIN".split(",")
     */
    String ROLE_SEPARATOR = ",";
}
