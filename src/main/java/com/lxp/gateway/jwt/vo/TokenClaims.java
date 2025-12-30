package com.lxp.gateway.jwt.vo;

import java.util.List;

public record TokenClaims(
    String userId,
    String email,
    List<String> authorities
) {
}
