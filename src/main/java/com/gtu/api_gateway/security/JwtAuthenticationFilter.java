package com.gtu.api_gateway.security;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.gtu.api_gateway.services.JwtService;

import reactor.core.publisher.Mono;



@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    private final JwtService jwtService;


    private final List<String> excludedPaths = List.of(
            "/api/auth",
            "/api/assign_driver",
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources"
    );

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String path = exchange.getRequest().getPath().value();
        System.out.println("Request Path: " + path);
        System.out.println("Authorization Header: " + authHeader);
        for (String excludedPath : excludedPaths) {
            if (path.contains(excludedPath)) {
                return chain.filter(exchange);
            }
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}
