package com.lxp.gateway.passport.config;

import com.lxp.gateway.global.support.GatewayGuard;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Setter
@Configuration
@ConfigurationProperties(prefix = "passport.key")
public class KeyProperties {

    private String secretKey;
    @Getter
    private int durationMillis;

    @Bean
    public SecretKey jwtSecretKey() {
        GatewayGuard.requireNonBlank(secretKey, "jwt secret key cannot be null or empty");
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
