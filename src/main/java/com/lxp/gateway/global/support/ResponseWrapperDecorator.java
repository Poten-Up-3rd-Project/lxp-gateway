package com.lxp.gateway.global.support;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
public class ResponseWrapperDecorator extends ServerHttpResponseDecorator {

    private final ServerWebExchange exchange;
    private final ResponseWrapperHelper wrapperHelper;

    public ResponseWrapperDecorator(ServerWebExchange exchange, ResponseWrapperHelper helper) {
        super(exchange.getResponse());
        this.exchange = exchange;
        this.wrapperHelper = helper;
    }

    @Override
    public @NonNull Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
        if (Objects.nonNull(getStatusCode()) && getStatusCode().is2xxSuccessful()) {
            log.debug("Wrapping 2xx response for: {}", exchange.getRequest().getPath());
            return super.writeWith(Flux.from(body).buffer().flatMap(dataBuffers ->
                Mono.just(wrapperHelper.wrapResponse(dataBuffers, exchange.getResponse()))
            ).doOnError(e -> log.error("Error in response wrapping", e)));
        }
        log.debug("Skipping wrap for non-2xx: {}", getStatusCode());
        return super.writeWith(body);
    }
}
