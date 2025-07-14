// package com.smartdocs.api_gateway.filter;

// import com.smartdocs.api_gateway.util.JwtUtil;
// import io.jsonwebtoken.Claims;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cloud.gateway.filter.GatewayFilter;
// import org.springframework.cloud.gateway.filter.GatewayFilterChain;
// import org.springframework.core.Ordered;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Component;
// import org.springframework.web.server.ServerWebExchange;
// import reactor.core.publisher.Mono;

// @Component
// public class JwtAuthenticationGatewayFilter implements GatewayFilter, Ordered {

//     @Autowired
//     private JwtUtil jwtUtil;

//     @Override
//     public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//         String path = exchange.getRequest().getPath().value();
//         System.out.println(">>> API Gateway: JwtAuthenticationGatewayFilter called for " + exchange.getRequest().getMethod() + " " + path);

//         // Allow public endpoints
//         if (path.startsWith("/api/auth/")) {
//             return chain.filter(exchange);
//         }

//         String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//         System.out.println(">>> API Gateway: Authorization header: " + authHeader);

//         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//             System.out.println(">>> API Gateway: Missing or invalid Authorization header");
//             exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//             return exchange.getResponse().setComplete();
//         }

//         String token = authHeader.substring(7);

//         try {
//             Claims claims = jwtUtil.validateTokenAndGetClaims(token);
//             System.out.println(">>> API Gateway: JWT validated successfully for subject: " + claims.getSubject());
//             return chain.filter(exchange);
//         } catch (Exception e) {
//             System.out.println(">>> API Gateway: JWT validation failed: " + e.getMessage());
//             exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//             return exchange.getResponse().setComplete();
//         }
//     }

//     @Override
//     public int getOrder() {
//         return -1; // Run before default filters
//     }
// }


// package com.smartdocs.api_gateway.filter;

// import com.smartdocs.api_gateway.util.JwtUtil;
// import io.jsonwebtoken.Claims;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cloud.gateway.filter.GlobalFilter;
// import org.springframework.core.Ordered;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Component;
// import org.springframework.web.server.ServerWebExchange;
// import reactor.core.publisher.Mono;

// @Component
// public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {

//     @Autowired
//     private JwtUtil jwtUtil;

//     @Override
//     public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
//         String path = exchange.getRequest().getPath().value();
//         String method = exchange.getRequest().getMethodValue();
//         System.out.println(">>> API Gateway: JwtAuthenticationGatewayFilter called for " + method + " " + path);

//         // Allow public endpoints and OPTIONS
//         if (method.equalsIgnoreCase("OPTIONS") || path.startsWith("/api/auth/")) {
//             return chain.filter(exchange);
//         }

//         String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//         System.out.println(">>> API Gateway: Authorization header: " + authHeader);

//         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//             // Add CORS header for error responses
//             exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "http://localhost:3000");
//             System.out.println(">>> API Gateway: Missing or invalid Authorization header");
//             exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//             return exchange.getResponse().setComplete();
//         }

//         String token = authHeader.substring(7);

//         try {
//             Claims claims = jwtUtil.validateTokenAndGetClaims(token);
//             System.out.println(">>> API Gateway: JWT validated successfully for subject: " + claims.getSubject());
//             return chain.filter(exchange);
//         } catch (Exception e) {
//             // Add CORS header for error responses
//             exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "http://localhost:3000");
//             System.out.println(">>> API Gateway: JWT validation failed: " + e.getMessage());
//             exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//             return exchange.getResponse().setComplete();
//         }
//     }

//     @Override
//     public int getOrder() {
//         return -1; // Run before default filters
//     }
// }



// package com.smartdocs.api_gateway.filter;

// import com.smartdocs.api_gateway.util.JwtUtil;
// import io.jsonwebtoken.Claims;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cloud.gateway.filter.GlobalFilter;
// import org.springframework.core.Ordered;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Component;
// import org.springframework.web.server.ServerWebExchange;
// import reactor.core.publisher.Mono;

// @Component
// public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {

//     @Autowired
//     private JwtUtil jwtUtil;

//     private static final String ALLOWED_ORIGIN = "http://localhost:3000";

//     @Override
//     public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
//         String path = exchange.getRequest().getPath().value();
//         String method = exchange.getRequest().getMethod().name();
//         String origin = exchange.getRequest().getHeaders().getOrigin();
//         System.out.println(">>> API Gateway: JwtAuthenticationGatewayFilter called for " + method + " " + path);

//         // Always set CORS headers for allowed origin
//         if (ALLOWED_ORIGIN.equals(origin)) {
//             exchange.getResponse().getHeaders().set("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
//             exchange.getResponse().getHeaders().set("Vary", "Origin");
//         }

//         // Allow public endpoints and OPTIONS
//         if (method.equalsIgnoreCase("OPTIONS") || path.startsWith("/api/auth/")) {
//             return chain.filter(exchange);
//         }

//         String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//         System.out.println(">>> API Gateway: Authorization header: " + authHeader);

//         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//             exchange.getResponse().getHeaders().set("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
//             exchange.getResponse().getHeaders().set("Vary", "Origin");
//             System.out.println(">>> API Gateway: Missing or invalid Authorization header");
//             exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//             return exchange.getResponse().setComplete();
//         }
//         else {
//             System.out.println(">>> API Gateway: Valid Authorization header found");
//         }

//         String token = authHeader.substring(7);

//         try {
//             Claims claims = jwtUtil.validateTokenAndGetClaims(token);
//             System.out.println(">>> API Gateway: JWT validated successfully for subject: " + claims.getSubject());
//             return chain.filter(exchange);
//         } catch (Exception e) {
//             exchange.getResponse().getHeaders().set("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
//             exchange.getResponse().getHeaders().set("Vary", "Origin");
//             System.out.println(">>> API Gateway: JWT validation failed: " + e.getMessage());
//             exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//             return exchange.getResponse().setComplete();
//         }
//     }

//     @Override
//     public int getOrder() {
//         return -1; // Run before default filters
//     }
// }



// package com.smartdocs.api_gateway.filter;

// import com.smartdocs.api_gateway.util.JwtUtil;
// import io.jsonwebtoken.Claims;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cloud.gateway.filter.GlobalFilter;
// import org.springframework.core.Ordered;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Component;
// import org.springframework.web.server.ServerWebExchange;
// import reactor.core.publisher.Mono;

// @Component
// public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {

//     @Autowired
//     private JwtUtil jwtUtil;

//     // private static final String ALLOWED_ORIGIN = "http://localhost:3000";

//     @Override
//     public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
//         String path = exchange.getRequest().getPath().value();
//         String method = exchange.getRequest().getMethod().name();
//         String origin = exchange.getRequest().getHeaders().getOrigin();
//         System.out.println(">>> API Gateway: JwtAuthenticationGatewayFilter called for " + method + " " + path);

//         // Allow public endpoints and OPTIONS
//         if (method.equalsIgnoreCase("OPTIONS") || path.startsWith("/api/auth/")) {
//             return chain.filter(exchange);
//         }

//         String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//         System.out.println(">>> API Gateway: Authorization header: " + authHeader);

//         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//             System.out.println(">>> API Gateway: Missing or invalid Authorization header");
//             exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//             return exchange.getResponse().setComplete();
//         } else {
//             System.out.println(">>> API Gateway: Valid Authorization header found");
//         }

//         String token = authHeader.substring(7);

//         try {
//             Claims claims = jwtUtil.validateTokenAndGetClaims(token);
//             System.out.println(">>> API Gateway: JWT validated successfully for subject: " + claims.getSubject());
//             return chain.filter(exchange);
//         } catch (Exception e) {
//             System.out.println(">>> API Gateway: JWT validation failed: " + e.getMessage());
//             exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//             return exchange.getResponse().setComplete();
//         }
//     }

//     @Override
//     public int getOrder() {
//         return -1; // Run before default filters
//     }
// }





package com.smartdocs.api_gateway.filter;

import com.smartdocs.api_gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    private static final String ALLOWED_ORIGIN = "http://localhost:3000";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();
        System.out.println(">>> API Gateway: JwtAuthenticationGatewayFilter called for " + method + " " + path);

        // Allow OPTIONS and public auth endpoints
        if (method.equalsIgnoreCase("OPTIONS") || path.startsWith("/api/auth/")) {
            System.out.println(">>> Skipping JWT validation for OPTIONS or /api/auth/* request");
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        System.out.println(">>> API Gateway: Authorization header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println(">>> API Gateway: Missing or invalid Authorization header");
            return unauthorizedResponse(exchange);
        }

        String token = authHeader.substring(7); // Strip "Bearer "

     try {
    Claims claims = jwtUtil.validateTokenAndGetClaims(token);

    String userId = claims.getSubject(); // sub = username
    String role = (String) claims.get("role");

    System.out.println(">>> API Gateway: JWT validated successfully. User ID = " + userId + ", Role = " + role);

    // 🔥 Correct header mutation
  ServerWebExchange mutatedExchange = exchange.mutate()
    .request(builder -> builder.headers(httpHeaders -> {
        httpHeaders.set("X-User-Id", userId);
        httpHeaders.set("X-User-Role", role);
    }))
    .build();

    return chain.filter(mutatedExchange);
} catch (Exception e) {
    System.out.println(">>> API Gateway: JWT validation failed: " + e.getMessage());
    return unauthorizedResponse(exchange);
}

    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().set("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // High priority
    }
}
