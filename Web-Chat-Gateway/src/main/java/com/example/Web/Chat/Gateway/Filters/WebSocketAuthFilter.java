package com.example.Web.Chat.Gateway.Filters;

import com.example.Web.Chat.Gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class WebSocketAuthFilter extends AbstractGatewayFilterFactory<WebSocketAuthFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public WebSocketAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            URI uri = exchange.getRequest().getURI();
            String path = uri.getPath();

            if (path.contains("/ws")) { // Only apply for websocket endpoint
                MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(uri).build().getQueryParams();
                String token = queryParams.getFirst("token");

                if (token == null || token.isBlank()) {
                    return onError(exchange, "Missing token in query params", HttpStatus.UNAUTHORIZED);
                }

                try {
                        if (jwtUtil.validateToken(token)) {
                        System.out.println("✅ WebSocket Token Validated");
                    } else {
                        return onError(exchange, "Invalid WebSocket token", HttpStatus.UNAUTHORIZED);
                    }
                } catch (Exception e) {
                    return onError(exchange, "Error while validating WebSocket token", HttpStatus.UNAUTHORIZED);
                }
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(err.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}

