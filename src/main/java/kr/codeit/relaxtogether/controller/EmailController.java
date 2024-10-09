package kr.codeit.relaxtogether.controller;

import kr.codeit.relaxtogether.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send-email")
    public ResponseEntity<String> mailSend(String email) {
        emailService.sendEmail(email);
        return ResponseEntity
            .ok("success");
    }

    @GetMapping("/verify-link")
    public ResponseEntity<Boolean> verifyLink(String token) {
        return ResponseEntity
            .ok(emailService.verifyLink(token));
    }
}
