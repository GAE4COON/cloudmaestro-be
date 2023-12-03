package com.gae4coon.cloudmaestro.global.config;
import com.gae4coon.cloudmaestro.global.config.exception.JwtAccessDeniedHandler;
import com.gae4coon.cloudmaestro.global.config.exception.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.Arrays;
import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringConfig {
    private final JwtTokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private static final String[] WHITE_LIST = {
            "/api/v1/users-api/**",
            "/api/v1/test-api/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        http
                .formLogin(form -> form.disable())
                .csrf(c -> c.disable())
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .headers(c -> c.frameOptions(f -> f.disable()).disable())
                .authorizeHttpRequests(auth -> {
                    try {
                        auth
                                .requestMatchers(WHITE_LIST).permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .anyRequest().authenticated();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .apply(new JWTConfig(tokenProvider));


        return http.build();
    }

    @Bean
    public CorsConfigurationSource  corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://ec2-3-39-133-88.ap-northeast-2.compute.amazonaws.com:3000",
                "http://cloudmaestro.site:3000",
                "http://cloudmaestro.site:80",
                "http://cloudmaestro.site",
                "https://cloudmaestro.site"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("X-Requested-With", "Content-Type", "Authorization", "X-XSRF-token"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}