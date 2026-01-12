package com.lxp.gateway.passport.model;

import java.util.List;

public record Passport(
    String userId,
    List<String> role,
    String traceId
) {
}
