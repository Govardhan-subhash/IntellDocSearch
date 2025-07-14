// package com.smartdocs.user_service.controller;

// import com.smartdocs.user_service.model.User;
// import com.smartdocs.user_service.service.AuthService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.Map;

// @RestController
// @RequestMapping("/auth")
// public class AuthController {
//     @Autowired
//     private AuthService authService;

//     @PostMapping("/register")
//     public ResponseEntity<String> register(@RequestBody User user) {
//         return ResponseEntity.ok(authService.register(user));
//     }

//     @PostMapping("/login")
//     public ResponseEntity<String> login(@RequestBody Map<String, String> loginRequest) {
//         String username = loginRequest.get("username");
//         String password = loginRequest.get("password");
//         return ResponseEntity.ok(authService.login(username, password));
//     }
// }


package com.smartdocs.user_service.controller;

import com.smartdocs.user_service.model.User;
import com.smartdocs.user_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        String result = authService.register(user);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        String token = authService.login(username, password);
        System.out.println(token);
        return ResponseEntity.ok(token);
    }
}
