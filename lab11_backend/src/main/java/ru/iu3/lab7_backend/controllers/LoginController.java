package ru.iu3.lab7_backend.controllers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.iu3.lab7_backend.models.User;
import ru.iu3.lab7_backend.repositories.UserRepository;
import ru.iu3.lab7_backend.tools.Utils;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.bind.annotation.CrossOrigin;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> credentials) {
        String login = credentials.get("login");
        String pwd = credentials.get("password");
        if (!pwd.isEmpty() && !login.isEmpty()) {
            Optional<User> uu = userRepository.findByLogin(login);
            if (uu.isPresent()) {
                User u2 = uu.get();
                String hash1 = u2.password;
                String salt = u2.salt;
                String hash2 = Utils.ComputeHash(pwd, salt);
                if (hash1.toLowerCase().equals(hash2.toLowerCase())) {
                    String token = UUID.randomUUID().toString();
                    u2.token = token;
                    u2.activity = LocalDateTime.now();
                    User u3 = userRepository.saveAndFlush(u2);
                    Object uToSend = new Object() {
                        @JsonProperty("token")
                        String token = u3.token;
                        @JsonProperty("user")
                        User user = u3;
                    };
                    return new ResponseEntity<Object>(uToSend, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<Object>(HttpStatus.UNAUTHORIZED);
    }


    @GetMapping("/logout")
    public ResponseEntity<Object> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && !token.isEmpty()) {
            token = StringUtils.removeStart(token, "Bearer").trim();
            Optional<User> uu = userRepository.findByToken(token);
            if (uu.isPresent()) {
                User u = uu.get();
                u.token = null;
                userRepository.save(u);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}