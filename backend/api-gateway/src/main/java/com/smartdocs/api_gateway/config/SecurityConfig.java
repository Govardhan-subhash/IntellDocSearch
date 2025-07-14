// // package com.smartdocs.api_gateway.config;

// // import org.springframework.context.annotation.Bean;
// // import org.springframework.context.annotation.Configuration;
// // import org.springframework.http.HttpMethod;
// // import org.springframework.security.config.web.server.ServerHttpSecurity;
// // import org.springframework.security.web.server.SecurityWebFilterChain;

// // @Configuration
// // public class SecurityConfig {

// //     @Bean
// //     public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
// //         System.out.println(">>> API Gateway: SecurityWebFilterChain initialized");
// //         http
// //             .csrf(ServerHttpSecurity.CsrfSpec::disable)
// //             .authorizeExchange(exchanges -> exchanges
// //                 .pathMatchers(HttpMethod.OPTIONS).permitAll()         // Allow CORS preflight
// //                 .pathMatchers("/api/auth/**").permitAll()  
// //                 .pathMatchers().permitAll()           // Allow public auth
// //                 // .anyExchange().authenticated()                        // All others secured
// //             )
// //             .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable); // Disable HTTP Basic!
// //         return http.build();
// //     }
// // }


// // package com.smartdocs.api_gateway.config;

// // import org.springframework.context.annotation.Bean;
// // import org.springframework.context.annotation.Configuration;
// // import org.springframework.security.config.web.server.ServerHttpSecurity;
// // import org.springframework.security.web.server.SecurityWebFilterChain;

// // @Configuration
// // public class SecurityConfig {

// //     @Bean
// //     public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
// //         System.out.println(">>> API Gateway: SecurityWebFilterChain initialized");
// //         http
// //             .csrf(ServerHttpSecurity.CsrfSpec::disable)
// //             .authorizeExchange(exchanges -> exchanges
// //                 .anyExchange().permitAll()  // Allow all endpoints
// //             )
// //             .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);  // Disable HTTP Basic Authentication
// //         return http.build();
// //     }
// // }


// // package com.smartdocs.api_gateway.config;

// // import org.springframework.context.annotation.Bean;
// // import org.springframework.context.annotation.Configuration;
// // import org.springframework.security.config.web.server.ServerHttpSecurity;
// // import org.springframework.security.web.server.SecurityWebFilterChain;

// // @Configuration
// // public class SecurityConfig {

// //     @Bean
// //     public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
// //         System.out.println(">>> API Gateway: SecurityWebFilterChain initialized");

// //         http.csrf(ServerHttpSecurity.CsrfSpec::disable)
// //             .authorizeExchange(exchanges -> exchanges
// //                 .anyExchange().permitAll()  // Allow all endpoints
// //             )
// //             .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)  // Disable HTTP Basic Authentication
// //             .cors()  // Enable CORS (handled by Spring Security)
// //             .and();

// //         return http.build();
// //     }
// // }


// package com.smartdocs.api_gateway.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.web.server.ServerHttpSecurity;
// import org.springframework.security.web.server.SecurityWebFilterChain;

// @Configuration
// public class SecurityConfig {

//     @Bean
// public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//     System.out.println(">>> API Gateway: SecurityWebFilterChain initialized");

//     http
//         .csrf(ServerHttpSecurity.CsrfSpec::disable)
//         .authorizeExchange(exchanges -> exchanges
//             .anyExchange().permitAll()
//         )
//         .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
//         // ⛔ REMOVE .cors()

//     return http.build();
// }

// }
