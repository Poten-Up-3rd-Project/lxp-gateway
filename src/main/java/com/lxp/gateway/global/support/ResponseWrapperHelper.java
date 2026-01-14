package com.lxp.gateway.global.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.common.infrastructure.exception.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseWrapperHelper {

    private final ObjectMapper objectMapper;

    public DataBuffer wrapResponse(List<? extends DataBuffer> dataBuffers, ServerHttpResponse response) {
        byte[] joinedBytes = joinDataBuffers(dataBuffers);
        
        // 응답 본문이 비어있으면 원본 반환
        if (joinedBytes.length == 0) {
            return response.bufferFactory().wrap(joinedBytes);
        }
        
        try {
            Object originalData = objectMapper.readValue(joinedBytes, Object.class);
            ApiResponse<?> wrapped = ApiResponse.success(originalData);
            byte[] wrappedBytes = objectMapper.writeValueAsBytes(wrapped);

            response.getHeaders().setContentLength(wrappedBytes.length);
            return response.bufferFactory().wrap(wrappedBytes);
        } catch (Exception e) {
            log.warn("Failed to wrap response: {}", e.getMessage(), e);
            return response.bufferFactory().wrap(joinedBytes);
        }
    }

    private byte[] joinDataBuffers(List<? extends DataBuffer> dataBuffers) {
        int totalLength = dataBuffers.stream().mapToInt(DataBuffer::readableByteCount).sum();
        byte[] result = new byte[totalLength];
        int offset = 0;

        try {
            for (DataBuffer db : dataBuffers) {
                byte[] bytes = new byte[db.readableByteCount()];
                db.read(bytes);
                System.arraycopy(bytes, 0, result, offset, bytes.length);
                offset += bytes.length;
            }
        } finally {
            dataBuffers.forEach(DataBufferUtils::release);
        }

        return result;
    }

}
