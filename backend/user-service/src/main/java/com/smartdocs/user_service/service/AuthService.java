// package com.smartdocs.user_service.service;

// import com.smartdocs.user_service.model.User;
// import com.smartdocs.user_service.repository.UserRepository;
// import com.smartdocs.user_service.util.JwtUtil;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// import java.util.Optional;

// @Service
// public class AuthService {

//     @Autowired
//     private UserRepository userRepository;

//     @Autowired
//     private PasswordEncoder passwordEncoder;

//     @Autowired
//     private JwtUtil jwtUtil;

//     public String register(User user) {
//         user.setPassword(passwordEncoder.encode(user.getPassword()));
//         userRepository.save(user);
//         return "User registered successfully!";
//     }

//     public String login(String username, String password) {
//         Optional<User> user = userRepository.findByUsername(username);
//         if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
//             return jwtUtil.generateToken(username, user.get().getRole());
//         }
//         throw new RuntimeException("Invalid username or password");
//     }
// }


package com.smartdocs.user_service.service;

import com.smartdocs.user_service.model.User;
import com.smartdocs.user_service.repository.UserRepository;
import com.smartdocs.user_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully!";
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("Invalid username or password"));
;
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate token with MongoDB _id as subject
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

}
