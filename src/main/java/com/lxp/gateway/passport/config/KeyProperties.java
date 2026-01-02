package com.lxp.gateway.passport.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@ConfigurationProperties(prefix = "passport.key")
public class KeyProperties {

    private String privateKeyString;
    private String publicKeyString;

    @Getter
    private int durationMillis;

    public KeyProperties(String privateKeyString,
                         String publicKeyString,
                         int durationMillis) {
        this.privateKeyString = privateKeyString;
        this.publicKeyString = publicKeyString;
        this.durationMillis = durationMillis;
    }

    public PrivateKey getPrivateKey() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public PublicKey getPublicKey() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

}
