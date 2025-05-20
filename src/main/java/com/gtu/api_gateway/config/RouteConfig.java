package com.gtu.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gtu.api_gateway.security.JwtAuthenticationFilter;

@Configuration
public class RouteConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private static final String SEGM_STRING = "/${segment}";

    public RouteConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()  
            .route("route-management", r -> r
                .path("/api/route-management/**")
                .filters(f -> f
                    .filter(jwtFilter) 
                    .rewritePath("/api/route-management/(?<segment>.*)", SEGM_STRING   )
                )
                .uri("lb://gtu-route-management-service"))
            
            .route("user-management", r -> r
                .path("/api/user-management/**")
                .filters(f -> f
                    .filter(jwtFilter) 
                    .rewritePath("/api/user-management/(?<segment>.*)", SEGM_STRING)
                )
                .uri("lb://gtu-users-management-service"))

            .route("auth", r -> r
                .path("/api/auth/**")
                .filters(f -> f
                    .rewritePath("/api/auth/(?<segment>.*)", SEGM_STRING)
                )
                .uri("lb://gtu-auth-service"))

            .route("assign_drvier", r -> r
                .path("/api/assign_driver/**")
                .filters(f -> f
                    .rewritePath("/api/assign_driver/(?<segment>.*)", SEGM_STRING)
                )
                .uri("lb://gtu-assign-driver-service"))
            .build();
    }
}