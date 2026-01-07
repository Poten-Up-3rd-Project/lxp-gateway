package com.lxp.gateway.jwt.config;

import com.lxp.gateway.global.exception.GatewayErrorCode;
import com.lxp.gateway.global.exception.GatewayException;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Setter
public class JwtConfig {

    private String secretKey;

    @Bean
    public SecretKey jwtSecretKey() {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new GatewayException(GatewayErrorCode.UNAUTHORIZED_ACCESS, "JWT Secret Key must be configured.");
        }
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

}
