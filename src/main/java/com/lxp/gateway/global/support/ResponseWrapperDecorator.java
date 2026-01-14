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
        log.debug("writeWith called for: {}, status: {}", exchange.getRequest().getPath(), getStatusCode());
        
        if (Objects.nonNull(getStatusCode())) {
            log.debug("Wrapping response for: {}", exchange.getRequest().getPath());
            return super.writeWith(Flux.from(body)
                .doOnNext(buffer -> log.debug("Received buffer with {} bytes", buffer.readableByteCount()))
                .collectList()
                .doOnNext(buffers -> log.debug("Collected {} data buffers", buffers.size()))
                .flatMapMany(dataBuffers ->
                    Flux.just(wrapperHelper.wrapResponse(dataBuffers, exchange.getResponse()))
                )
                .doOnError(e -> log.error("Error in response wrapping", e)));
        }
        log.debug("No status code, passing through");
        return super.writeWith(body);
    }
}
