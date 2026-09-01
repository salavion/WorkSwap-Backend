package org.workswap.sso.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
    
    @GetMapping
    public String redirectToAuth() {
        return "redirect:/auth";
    }

    @GetMapping("/auth")
    public String getAuthPage() {
        return "auth-page";
    }
}
