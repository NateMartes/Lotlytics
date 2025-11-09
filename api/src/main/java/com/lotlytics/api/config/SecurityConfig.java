package com.lotlytics.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import java.util.Arrays;
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
    private static final String LIST_SEPERATOR = ",";

    public SecurityConfig(SecurityProperties properties) {
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

    /**
     * The corsConfigurationSource method returns a pre configured CorsConfigurationSource.
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        try {
            config.setAllowedOrigins(
                Arrays.asList((properties.getCorsAllowOrigin()).split(LIST_SEPERATOR)
            ));
            config.setAllowedMethods(
                Arrays.asList(properties.getCorsAllowMethods().split(LIST_SEPERATOR)
            ));
            config.setAllowedHeaders(
                Arrays.asList(properties.getCorsAllowHeaders().split(LIST_SEPERATOR)
            ));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid CORS configuration: " + e.getMessage(), e);
        }

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * The securityFilterChain method returns a pre configured SecurityFilterChain.
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable());

        return http.build();
    }
}
