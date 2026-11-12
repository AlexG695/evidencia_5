package com.utch.cafeteria.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Permite al frontend confirmar la identidad y el rol del usuario autenticado
 * despues de iniciar sesion con HTTP Basic.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Map.of(
                "username", auth.getName(),
                "roles", roles
        );
    }
}
