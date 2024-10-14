package kr.codeit.relaxtogether.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.codeit.relaxtogether.dto.email.request.CodeVerificationRequest;
import kr.codeit.relaxtogether.dto.email.request.SendEmailRequest;
import kr.codeit.relaxtogether.dto.email.request.TokenVerificationRequest;
import kr.codeit.relaxtogether.dto.email.response.TokenVerificationResponse;
import kr.codeit.relaxtogether.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "이메일 발송(비밀번호 변경)", description = "비밀번호 변경 링크를 담은 이메일을 발송합니다.")
    @PostMapping("/email/password-change")
    public ResponseEntity<String> sendEmailForPasswordChange(@RequestBody SendEmailRequest sendEmailRequest) {
        emailService.sendEmailForPasswordChange(sendEmailRequest.getEmail());
        return ResponseEntity
            .ok("success");
    }

    @Operation(summary = "토큰 검증", description = "링크를 통해 넘어온 토큰을 검증합니다.")
    @PostMapping("/verify-token")
    public ResponseEntity<TokenVerificationResponse> verifyToken(
        @RequestBody TokenVerificationRequest tokenVerificationRequest) {
        return ResponseEntity
            .ok(emailService.verifyToken(tokenVerificationRequest.getToken()));
    }

    @Operation(summary = "이메일 발송(회원가입 진행)", description = "회원가입 진행을 위한 인증 코드를 담은 이메일을 발송합니다.")
    @PostMapping("/email/signup")
    public ResponseEntity<String> sendEmailForSignup(@RequestBody SendEmailRequest sendEmailRequest) {
        emailService.sendEmailForSignup(sendEmailRequest.getEmail());
        return ResponseEntity
            .ok("success");
    }

    @Operation(summary = "인증번호 검증", description = "회원가입 인증번호를 검증합니다.")
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody CodeVerificationRequest codeVerificationRequest) {
        emailService.verifyCode(codeVerificationRequest.getCode());
        return ResponseEntity
            .ok("success");
    }
}
