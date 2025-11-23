package com.lotlytics.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lotlytics.api.services.PasswordService;
import com.lotlytics.api.services.UserService;

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
@EnableWebSecurity
public class SecurityConfig {
    
    private SecurityProperties properties;
    private static final String LIST_SEPERATOR = ",";
    private UserService userService;
    private PasswordService passwordService;
    private OncePerRequestFilter filter;

    public SecurityConfig(SecurityProperties properties, UserService userService, PasswordService passwordService, OncePerRequestFilter filter) {
        this.properties = properties;
        this.userService = userService;
        this.passwordService = passwordService;
        this.filter = filter;
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
            config.setAllowCredentials(properties.getCorsAllowCredentialBoolean());
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
            .authorizeHttpRequests(auth -> 
                    auth
                    .requestMatchers("/login", "/me").authenticated()
                    .anyRequest().permitAll()
                )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(httpBasic -> httpBasic.disable())
            .authenticationProvider(authenticationProvider())
            .formLogin(form -> form.disable())
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /* 
     * The authenticationProvider method provides an authentication provider configuration
     * Links UserDetailsService and PasswordEncoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordService);
        return provider;
    }


    /* 
     * Authentication manager bean
     * Required for programmatic authentication (e.g., in /generateToken)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
