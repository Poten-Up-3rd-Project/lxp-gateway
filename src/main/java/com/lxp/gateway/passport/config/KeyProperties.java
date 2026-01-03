package com.lxp.gateway.passport.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@ConfigurationProperties(prefix = "passport.key")
public class KeyProperties {

    private String privateKeyString;

    @Getter
    private int durationMillis;

    public KeyProperties(String privateKeyString, int durationMillis) {
        this.privateKeyString = privateKeyString;
        this.durationMillis = durationMillis;
    }

    public PrivateKey getPrivateKey() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

}
