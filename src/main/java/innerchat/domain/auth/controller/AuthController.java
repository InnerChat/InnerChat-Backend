package innerchat.domain.auth.controller;

import innerchat.domain.auth.dto.LoginRequest;
import innerchat.domain.auth.dto.LoginResponse;
import innerchat.domain.auth.dto.RegisterRequest;
import innerchat.domain.auth.dto.RegisterResponse;
import innerchat.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpSession session) {
        return authService.login(request.getId(), request.getPassword(), session);
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        authService.logout(session);
    }
}
