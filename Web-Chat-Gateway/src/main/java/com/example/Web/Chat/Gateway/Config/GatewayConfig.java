//package com.example.Web.Chat.Gateway.Config;
//
////import com.example.Web.Chat.Gateway.Filters.JwtAuthFilter;
//import com.example.Web.Chat.Gateway.Filters.JwtAuthFilter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GatewayConfig {
////    @Autowired
////    private JwtAuthFilter authFilter;
//
//    @Bean
//    public RouteLocator routes(RouteLocatorBuilder builder,JwtAuthFilter jwtAuthFilter) {
//        return builder.routes()
//                .route("auth-service", r -> r.path("/auth/**")
//                        .uri("http://localhost:8081"))
//                .route("user-service", r -> r.path("/users/**")
//                        .filters(f -> f.filter(jwtAuthFilter))
//                        .uri("http://localhost:8082"))
//                .build();
//    }
//}
