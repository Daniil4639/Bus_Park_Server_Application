package app.controllers;

import app.dto.users.BusUserDto;
import app.security.jwt.JwtService;
import app.security.user_details.BusUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {

    private final BusUserService userService;

    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<String> login(@RequestBody BusUserDto userDto) {
        try {
            UserDetails user = userService.loadUserByUsername(userDto.getUsername());
            return ResponseEntity.ok(jwtService.generateToken(user));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}