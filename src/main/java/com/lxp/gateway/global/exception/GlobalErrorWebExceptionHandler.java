package com.lxp.gateway.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.common.domain.exception.DomainException;
import com.lxp.common.infrastructure.exception.ErrorResponse;
import com.lxp.common.infrastructure.exception.GlobalExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.lxp.gateway.global.exception.GatewayErrorCode.INTERNAL_ERROR;

@Component
@Order(-1)
@RequiredArgsConstructor
public class GlobalErrorWebExceptionHandler extends GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if (ex instanceof DomainException domainEx) {
            HttpStatus status = mapToHttpStatus(domainEx.getGroup());
            response.setStatusCode(status);

            ErrorResponse errorResponse = ErrorResponse.from(domainEx);
            return writeResponse(response, errorResponse);
        }

        // 기타 예외 처리
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        ErrorResponse errorResponse = new ErrorResponse(
            INTERNAL_ERROR.getCode(),
            INTERNAL_ERROR.getMessage(),
            INTERNAL_ERROR.getGroup()
        );
        return writeResponse(response, errorResponse);
    }

    private HttpStatus mapToHttpStatus(String group) {
        if (group == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return switch (group.toUpperCase()) {
            case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "BAD_REQUEST", "INVALID" -> HttpStatus.BAD_REQUEST;
            case "CONFLICT" -> HttpStatus.CONFLICT;
            case "FORBIDDEN" -> HttpStatus.FORBIDDEN;
            case "UNAUTHORIZED" -> HttpStatus.UNAUTHORIZED;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private Mono<Void> writeResponse(ServerHttpResponse response, ErrorResponse error) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(error);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
