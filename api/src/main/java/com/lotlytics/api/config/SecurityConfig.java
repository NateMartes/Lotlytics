package com.lotlytics.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * The SecurityConfig class uses the SecurityProperties class to create beans of
 * pre configured compoments.
 * 
 * @see SecurityProperties
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {
    
    private SecurityProperties properties;

    public SecurityConfig(SecurityProperties properties) {
        this.properties = properties;
    }

    /**
     * The BCryptPasswordEncoderConfig returns a pre configured BCryptPasswordEncoder.
     * @return BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder BCryptPasswordEncoderConfig() {
        return new BCryptPasswordEncoder(properties.getPasswordStrength());
    }
}
