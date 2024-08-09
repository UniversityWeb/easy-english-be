package com.universityweb.common.security;

public class SecurityUtils {

    public static final String[] PERMIT_ALL_URLS = {
            "/",
            "/v1/api/**",
            "v1/api/auth/register/**",
            "v1/api/auth/login/**"
    };

    public static final String[] SWAGGER_URLS = {
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };

    private SecurityUtils() {
    }
}
