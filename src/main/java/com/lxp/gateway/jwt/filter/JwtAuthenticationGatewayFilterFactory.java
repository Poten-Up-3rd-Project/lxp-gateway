package com.lxp.gateway.jwt.filter;

import com.lxp.gateway.global.constants.MDCConstants;
import com.lxp.gateway.jwt.adapter.AuthHeaderResolver;
import com.lxp.gateway.jwt.adapter.PassportMapper;
import com.lxp.gateway.jwt.policy.JwtPolicy;
import com.lxp.gateway.jwt.policy.TokenRevocationPolicy;
import com.lxp.gateway.jwt.vo.TokenClaims;
import com.lxp.gateway.passport.component.PassportEncoder;
import com.lxp.gateway.passport.model.Passport;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final AuthHeaderResolver authHeaderResolver;
    private final JwtPolicy jwtPolicy;
    private final TokenRevocationPolicy revocationPolicy;
    private final PassportEncoder passportEncoder;
    private final PassportMapper passportMapper;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String token = authHeaderResolver.resolveToken(request);

            if (token == null || !jwtPolicy.validateToken(token) || revocationPolicy.isTokenBlacklisted(token)) {
                return onError(exchange, "Token has been revoked", HttpStatus.UNAUTHORIZED);
            }

            TokenClaims tokenClaims = jwtPolicy.parseToken(token);
            return Mono.deferContextual(ctx -> {
                String traceId = ctx.getOrDefault(MDCConstants.TRACE_ID, "unknown");
                Passport passport = passportMapper.toPassport(tokenClaims, traceId);
                String encodedPassport = passportEncoder.encode(passport);

                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-Passport", encodedPassport)
                    .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            });
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        byte[] bytes = err.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }
}
