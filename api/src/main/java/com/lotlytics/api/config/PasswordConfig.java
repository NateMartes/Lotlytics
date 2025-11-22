package com.lotlytics.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class PasswordConfig {
    
    SecurityProperties properties;

    public PasswordConfig(SecurityProperties properties) {
        this.properties = properties;
    }

    /**
     * The BCryptPasswordEncoderConfig method returns a pre configured BCryptPasswordEncoder.
     * @return BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder BCryptPasswordEncoderConfig() {
        return new BCryptPasswordEncoder(properties.getPasswordStrength());
    }
}
