package com.lxp.gateway.global.config;

import com.lxp.gateway.jwt.config.JwtConfig;
import com.lxp.gateway.passport.config.KeyProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(basePackageClasses = {KeyProperties.class, JwtConfig.class})
public class AppConfig {
}
