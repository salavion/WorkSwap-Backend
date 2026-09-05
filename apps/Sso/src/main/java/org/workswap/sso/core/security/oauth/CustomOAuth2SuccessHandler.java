package org.workswap.sso.core.security.oauth;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.sso.core.security.service.AuthCookiesService;
import org.workswap.sso.core.user.UserCommandService;
import org.workswap.sso.datasource.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserCommandService userCommandService;

    private final AuthCookiesService cookiesService;

    @Value("${frontend.url}")
    private String baseUrl;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws ServletException, IOException  {

        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        Optional<String> tempUserSub = Optional.ofNullable(request.getSession().getAttribute("tempUserSub"))
            .map(Object::toString);


        Optional<String> optionalRedirectUrl = Optional.ofNullable(request.getSession().getAttribute("redirectUrl"))
            .map(Object::toString);

        String redirect = optionalRedirectUrl.isPresent() ? optionalRedirectUrl.get() : baseUrl;

        System.out.println("Редирект " + redirect);

        User user = userCommandService.registerOrUpdateUser(oidcUser, tempUserSub, request);
        cookiesService.setAuthCookies(response, user);

        response.sendRedirect(redirect);
    }
}