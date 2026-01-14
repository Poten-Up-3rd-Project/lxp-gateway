package com.lxp.gateway.global.filter;

import com.lxp.gateway.global.constants.MDCConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@Slf4j
@Component
public class GatewayLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        HttpMethod method = exchange.getRequest().getMethod();
        URI uri = exchange.getRequest().getURI();
        log.info("Request started: {} {}, traceId: {}", method, uri, traceId);

        return chain.filter(exchange)
            .contextWrite(ctx -> ctx.put(MDCConstants.TRACE_ID, traceId))
            .doFinally(signalType -> {
                long duration = System.currentTimeMillis() - startTime;
                log.info(
                    "Request completed: {} {}, traceId: {}, duration: {}ms, status: {}",
                    method, uri, traceId, duration, exchange.getResponse().getStatusCode()
                );
                MDC.clear();
            });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
