package com.appointment.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Getter
@Setter
public class ApplicationProperties {
    private SecurityProps security;
    private CookieConfig cookie;

    @Getter
    @Setter
    public static class SecurityProps {
        private Jwt jwt;
        private CorsConfiguration cors;
        private PasswordConfig password;
        private LoginConfig login;
        private SessionConfig session;
    }

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private long tokenExpirationInMillis;
        private long renewTokenExpirationInMillis;
    }

    @Data
    public static class CookieConfig {
        private CookieAttribute attributes;
        private String domains;
    }

    @Data
    public static class CookieAttribute {
        private String sameSite;
        private boolean maxAge;
        private String path;
        private boolean httpOnly;
        private boolean secure;
    }

    @Data
    public static class PasswordConfig {
        private int adminExpiryDays;
        private int userExpiryDays;
        private int historyLimit;
        private int minLength;
        private int maxLength;
    }

    @Data
    public static class LoginConfig {
        private int maxAttempts;
        private int lockoutMinutes;
    }

    @Data
    public static class SessionConfig {
        private int timeoutMinutes;
        private int renewalMinutes;
        private int maxConcurrentSessions;
    }
}
