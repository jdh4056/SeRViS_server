package horizon.SeRVe.auth.controller;

import horizon.SeRVe.auth.dto.*;
import horizon.SeRVe.auth.entity.User;
import horizon.SeRVe.auth.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal User user) {
        authService.withdraw(user.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/public-key", produces = "application/json")
    public ResponseEntity<String> getPublicKey(@RequestParam String email) throws JsonProcessingException {
        String publicKey = authService.getPublicKey(email);
        // publicKey 자체가 JSON 객체 문자열이므로, JSON 문자열 리터럴로 감싸야
        // Python response.json()이 dict가 아닌 str로 파싱함
        return ResponseEntity.ok(new ObjectMapper().writeValueAsString(publicKey));
    }

    // 로봇 로그인 엔드포인트
    @PostMapping("/robot/login")
    public ResponseEntity<LoginResponse> robotLogin(@RequestBody @Valid RobotLoginRequest request) {
        LoginResponse response = authService.robotLogin(request);
        return ResponseEntity.ok(response);
    }
}
