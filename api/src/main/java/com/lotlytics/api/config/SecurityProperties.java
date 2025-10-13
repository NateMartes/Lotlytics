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
    private Integer passwordStrength;

}
