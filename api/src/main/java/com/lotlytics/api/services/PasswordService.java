package com.lotlytics.api.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.lotlytics.api.config.SecurityConfig;
/*
 * The PasswordService class provides service methods for hashing/checking passwords
 */
@Service
public class PasswordService {

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * The PasswordService uses the SecurityConfig to setup dependencies.
     * 
     * @see SecurityConfig
     * @param securityConfig The security configuration for that app.
     */
    public PasswordService(SecurityConfig securityConfig) {
        this.bCryptPasswordEncoder = securityConfig.BCryptPasswordEncoderConfig();
    }

    /**
     * The hashPassword method takes a String and hashes it using the BCrypt Password Encoder.
     * 
     * @param password A unhashed string.
     * @return The hashed string.
     */
    public String hashPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    /**
     * The compare method takes a hashed password and a unhased string and determines if they
     * are the same.
     * 
     * @param passwordHash The hashed password.
     * @param unHashedPassword The unhashed password.
     * @return true if and only if the unhased password is the same as the hased one.
     */
    public boolean compare(String passwordHash, String unHashedPassword) {
        return bCryptPasswordEncoder.matches(unHashedPassword, passwordHash);
    }
}
