package app.calendar.user.presentation;

import app.calendar.user.application.AuthenticationService;
import app.calendar.user.presentation.request.AuthenticationRequest;
import app.calendar.user.presentation.request.RegisterRequest;
import app.calendar.user.presentation.response.AuthenticationResponse;
import app.calendar.user.presentation.response.RegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserResource {
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/default")
    public String defaultTest() {
        return "Working";
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }
}