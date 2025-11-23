package com.lotlytics.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
/*
 * The SecurityProperties class aggregates the configuration
 * properties defined in the application.properties file for
 * handling security.
 *
 * Configuration keys:
 *   security.password-strength   – The strength of the password, defined by the BCryptPasswordEncoder.
 */
@Getter
@Setter
@ConfigurationProperties("security")
public class SecurityProperties {

    /** The strength of the password, defined by the BCryptPasswordEncoder */
    private Integer passwordStrength = 15;

    /** A comma seperated list of domains that are allowed to access this API */
    private String corsAllowOrigin = "*";

    /** A comma seperated list of methods that are allowed by this API */
    private String corsAllowMethods = "*";

    /** A comma seperated list of headers that are allowed by this API */
    private String corsAllowHeaders = "*";

    /** 'true' if we will allow credentials in our API */
    private String corsAllowCredential = "false";

    public Boolean getCorsAllowCredentialBoolean() throws IllegalArgumentException {
        try {
            return Boolean.parseBoolean(corsAllowCredential);
        } catch (Exception e) {
            throw new IllegalArgumentException("corsAllowCredential must be true or false");
        }
    }
}
