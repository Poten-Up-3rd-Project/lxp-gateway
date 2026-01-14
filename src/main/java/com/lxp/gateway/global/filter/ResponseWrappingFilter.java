package com.lxp.gateway.global.filter;

import com.lxp.gateway.global.support.ResponseWrapperDecorator;
import com.lxp.gateway.global.support.ResponseWrapperHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ResponseWrappingFilter implements GlobalFilter, Ordered {

    private final ResponseWrapperHelper wrapperHelper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange.mutate()
            .response(new ResponseWrapperDecorator(exchange, wrapperHelper))
            .build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
