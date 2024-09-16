package kr.codeit.relaxtogether.controller;

import jakarta.validation.Valid;
import kr.codeit.relaxtogether.dto.user.request.EmailCheckRequest;
import kr.codeit.relaxtogether.dto.user.request.JoinUserRequest;
import kr.codeit.relaxtogether.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auths")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@Valid @RequestBody EmailCheckRequest emailCheckRequest) {
        return ResponseEntity.ok(userService.checkEmail(emailCheckRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody JoinUserRequest joinUserRequest) {
        userService.signup(joinUserRequest);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("success");
    }
}
