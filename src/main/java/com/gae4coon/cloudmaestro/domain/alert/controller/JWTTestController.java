package com.gae4coon.cloudmaestro.domain.alert.controller;

import com.gae4coon.cloudmaestro.global.config.JWTAuthFillter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/alert-api")
@RequiredArgsConstructor
public class JWTTestController {

    private final JWTAuthFillter jwtAuthFillter;

    @GetMapping("/test")
    public ResponseEntity<Object> testCheck(Principal principal) {
        System.out.println("principal: "+principal);
        return ResponseEntity.ok().body(principal.getName());
    }
}
