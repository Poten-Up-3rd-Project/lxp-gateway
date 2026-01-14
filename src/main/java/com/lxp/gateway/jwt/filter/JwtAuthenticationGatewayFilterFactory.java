package com.lxp.gateway.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.common.infrastructure.exception.ApiResponse;
import com.lxp.common.infrastructure.exception.ErrorResponse;
import com.lxp.gateway.global.constants.MDCConstants;
import com.lxp.gateway.global.exception.GatewayErrorCode;
import com.lxp.gateway.jwt.exception.InvalidTokenException;
import com.lxp.gateway.jwt.policy.JwtPolicy;
import com.lxp.gateway.jwt.policy.TokenRevocationPolicy;
import com.lxp.gateway.jwt.support.AuthHeaderResolver;
import com.lxp.gateway.jwt.support.PassportMapper;
import com.lxp.gateway.jwt.vo.TokenClaims;
import com.lxp.gateway.passport.component.PassportEncoder;
import com.lxp.gateway.passport.model.Passport;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    public static final String PASSPORT = "X-Passport";

    private final AuthHeaderResolver authHeaderResolver;
    private final JwtPolicy jwtPolicy;
    private final TokenRevocationPolicy revocationPolicy;
    private final PassportEncoder passportEncoder;
    private final PassportMapper passportMapper;
    private final ObjectMapper objectMapper;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String token = authHeaderResolver.resolveToken(request);

            if (token == null || revocationPolicy.isTokenBlacklisted(token)) {
                return onError(exchange, GatewayErrorCode.UNAUTHORIZED_ACCESS);
            }

            TokenClaims tokenClaims;
            try {
                tokenClaims = jwtPolicy.verify(token);
            } catch (InvalidTokenException e) {
                return onError(exchange, GatewayErrorCode.UNAUTHORIZED_ACCESS);
            }
            return Mono.deferContextual(ctx -> {
                String traceId = ctx.getOrDefault(MDCConstants.TRACE_ID, UUID.randomUUID().toString());
                Passport passport = passportMapper.toPassport(tokenClaims, traceId);
                String encodedPassport = passportEncoder.encode(passport);

                ServerHttpRequest modifiedRequest = request.mutate()
                    .header(PASSPORT, encodedPassport)
                    .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            });
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, GatewayErrorCode errorCode) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.empty();
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return Mono.fromSupplier(() -> {
            try {
                ErrorResponse errorResponse = new ErrorResponse(
                    errorCode.getCode(),
                    errorCode.getMessage(),
                    errorCode.getGroup()
                );
                ApiResponse<?> wrappedResponse = ApiResponse.error(errorResponse);
                return objectMapper.writeValueAsBytes(wrappedResponse);
            } catch (Exception e) {
                return ("{\"success\":false,\"error\":{\"code\":500,\"message\":\"Internal Error\"}}").getBytes(StandardCharsets.UTF_8);
            }
        }).flatMap(bytes -> {
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        });
    }
}
