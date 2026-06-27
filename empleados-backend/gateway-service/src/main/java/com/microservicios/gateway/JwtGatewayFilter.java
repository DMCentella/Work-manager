package com.microservicios.gateway;

import com.microservicios.common.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtGatewayFilter extends AbstractGatewayFilterFactory<JwtGatewayFilter.Config> {

    public JwtGatewayFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register") || path.startsWith("/ws"))

            {


                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            try {
                String token = authHeader.substring(7);
                Claims claims = JwtUtil.validateToken(token);
                var mutatedRequest = exchange.getRequest().mutate()
                        .header("X-Username", claims.getSubject())
                        .header("X-Roles", String.join(",", claims.get("roles", List.class)))
                        .header("X-UserId", claims.get("userId", String.class))
                        .header("X-EmpleadoId", claims.get("empleadoId", String.class) != null
                                ? claims.get("empleadoId", String.class) : "")
                        .build();
                exchange = exchange.mutate().request(mutatedRequest).build();
            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {}
}
