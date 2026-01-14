package com.lxp.gateway.jwt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    private final String publicKeyString;

    public JwtConfig(String publicKeyString) {
        this.publicKeyString = publicKeyString;
    }

    public PublicKey getPublicKey() throws Exception {
        String key = publicKeyString
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

}
