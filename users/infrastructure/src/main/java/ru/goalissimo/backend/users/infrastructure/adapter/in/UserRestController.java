package ru.goalissimo.backend.users.infrastructure.adapter.in;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.goalissimo.backend.users.application.dto.UserRegisterResult;
import ru.goalissimo.backend.users.application.port.in.UserRegisterInPort;
import ru.goalissimo.backend.users.infrastructure.dto.web.UserRegisterRequest;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    private final UserRegisterInPort inPort;

    public UserRestController(UserRegisterInPort userRegisterInPort) {
        this.inPort = userRegisterInPort;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResult> register(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterResult result = inPort.register(request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
