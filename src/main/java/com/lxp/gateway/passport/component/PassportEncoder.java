package com.lxp.gateway.passport.component;

import com.lxp.gateway.passport.config.KeyProperties;
import com.lxp.gateway.passport.model.Passport;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Component
public class PassportEncoder {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public PassportEncoder(KeyProperties properties) throws Exception {
        this.privateKey = properties.getPrivateKey();
        this.publicKey = properties.getPublicKey();
    }

    /**
     * Passport 객체를 JWT 문자열로 변환
     *
     * @param passport
     * @return
     */
    public String encode(Passport passport) {
        Claims claims = Jwts.claims()
            .add("uid", passport.userId())
            .add("rol", passport.role())
            .add("tid", passport.traceId())
            .build();

        return Jwts.builder()
            .claims(claims)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 60000))
            .signWith(privateKey, Jwts.SIG.RS256)
            .compact();
    }

}
