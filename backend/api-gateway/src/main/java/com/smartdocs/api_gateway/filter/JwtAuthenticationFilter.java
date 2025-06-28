// package com.smartdocs.api_gateway.filter;

// import com.smartdocs.api_gateway.util.JwtUtil;
// import io.jsonwebtoken.Claims;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Component;
// import org.springframework.web.server.ServerWebExchange;
// import org.springframework.web.server.WebFilter;
// import org.springframework.web.server.WebFilterChain;
// import reactor.core.publisher.Mono;
// import org.springframework.http.HttpMethod;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// @Component
// public class JwtAuthenticationFilter implements WebFilter {

//     @Autowired
//     private JwtUtil jwtUtil;

//     private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
//     private static final String AUTHORIZATION_HEADER = "Authorization";

//     @Override
//     public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//         System.out.println(">>> API Gateway: JwtAuthenticationFilter called for " + exchange.getRequest().getMethod() + " " + exchange.getRequest().getPath());

//         String path = exchange.getRequest().getPath().value();

//         if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
//             System.out.println(">>> API Gateway: OPTIONS request - skipping authentication");
//             return chain.filter(exchange);
//         }

//         if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")) {
//             System.out.println(">>> API Gateway: Public endpoint accessed: " + path);
//             return chain.filter(exchange);
//         }

//         String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
//         System.out.println(">>> API Gateway: Authorization header: " + authHeader);

//         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//             System.out.println(">>> API Gateway: Missing or invalid Authorization header");
//             exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//             return exchange.getResponse().setComplete();
//         }

//         String token = authHeader.substring(7);

//         try {
//             System.out.println(">>> API Gateway: Validating JWT token: " + token);
//             Claims claims = jwtUtil.validateTokenAndGetClaims(token);
//             System.out.println(">>> API Gateway: JWT validated successfully for subject: " + claims.getSubject());
//             return chain.filter(exchange);
//         } catch (Exception e) {
//             System.out.println(">>> API Gateway: JWT validation failed: " + e.getMessage());
//             exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//             return exchange.getResponse().setComplete();
//         }
//     }
// }
