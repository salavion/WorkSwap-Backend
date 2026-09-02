package org.workswap.sso.security.service;

import java.io.IOException;
import java.lang.reflect.Method;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.workswap.sso.security.annotations.controllers.Authenticated;
import org.workswap.sso.security.annotations.controllers.PublicEndpoint;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;
import org.workswap.sso.security.annotations.controllers.RequiredRole;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Profile("api")
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws IOException {

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Method method = handlerMethod.getMethod();

        Package controllerPackage =
                method.getDeclaringClass().getPackage();

        if (!controllerPackage.getName().startsWith("org.workswap")) {
            return true;
        }
        
        log.debug("Handled method: {}", method.getName());

        boolean isPublic =
                method.isAnnotationPresent(PublicEndpoint.class);

        log.debug("Is public: {}", isPublic);

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        boolean authenticated =
                authentication != null
                        && authentication.isAuthenticated();

        boolean requiresAuthentication =
                method.isAnnotationPresent(Authenticated.class);

        boolean requiresPermission =
                method.isAnnotationPresent(RequiredPermission.class);

        boolean requiresRole =
                method.isAnnotationPresent(RequiredRole.class);

        int securityAnnotations =
                (isPublic ? 1 : 0)
                + (requiresAuthentication ? 1 : 0)
                + (requiresPermission ? 1 : 0)
                + (requiresRole ? 1 : 0);

        if (securityAnnotations == 0) {
            throw new IllegalStateException(
                    "Endpoint must have a security annotation: "
                    + method.getDeclaringClass().getName()
                    + "#"
                    + method.getName()
            );
        }

        if (securityAnnotations > 1) {
            throw new IllegalStateException(
                    "Endpoint has multiple security annotations: "
                    + method.getDeclaringClass().getName()
                    + "#"
                    + method.getName()
            );
        }

                    
        log.debug("Is authenticated: {}", authenticated);

        if (isPublic) {
            if (!authenticated) {
                response.setHeader(
                        "X-User-Refresh",
                        "true"
                );
            }

            return true;
        }

        if (!authenticated) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            return false;
        }

        if (requiresPermission && !checkPermission(authentication, method)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );

            return false;
        }

        if (requiresRole && !checkRole(authentication, method)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );

            return false;
        }

        if (requiresAuthentication && authenticated) {
            return true;
        }

        return true;
    }

    @PostConstruct
    public void init() {
        log.debug("AuthenticationInterceptor registered");
    }

    public boolean hasRole(Authentication authentication, String role) {

        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    public boolean hasPermission(Authentication authentication, String permission) {

        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(permission));
    }

    private boolean checkPermission(Authentication authentication, Method method) {

        RequiredPermission requiredPermission =
            method.getAnnotation(RequiredPermission.class);
        
        log.debug("Need permission: {}", requiredPermission.value());

        if (!hasPermission(authentication, requiredPermission.value())) {
            return false;
        }

        log.debug("User has permission");

        return true;
    }

    private boolean checkRole(Authentication authentication, Method method) {

        RequiredRole requiredRole =
            method.getAnnotation(RequiredRole.class);

        log.debug("Need role: {}", requiredRole.value());

        if (!hasRole(authentication, requiredRole.value())) {
            return false;
        }

        log.debug("User has role");

        return true;
    }
}
