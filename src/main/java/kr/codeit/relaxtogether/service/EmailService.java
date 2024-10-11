package kr.codeit.relaxtogether.service;

import static kr.codeit.relaxtogether.exception.ErrorCode.TOKEN_EXPIRED;
import static kr.codeit.relaxtogether.exception.ErrorCode.USER_NOT_FOUND;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.codeit.relaxtogether.auth.jwt.JwtUtil;
import kr.codeit.relaxtogether.dto.email.response.TokenVerificationResponse;
import kr.codeit.relaxtogether.entity.User;
import kr.codeit.relaxtogether.exception.ApiException;
import kr.codeit.relaxtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public void sendEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ApiException(USER_NOT_FOUND));

        String token = jwtUtil.createEmailVerificationToken(email);

        // 유저 이름과 요청 링크를 변수로 담음
        // 해당 Context를 템플릿에 담고 문자열로 변환
        Context context = new Context();
        context.setVariable("userName", user.getName());
        context.setVariable("resetLink", "https://relax-together.web.app/reset-password?token=" + token);
        String text = templateEngine.process("email.html", context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("같이달램 비밀번호 재설정 안내");
            helper.setText(text, true);
            helper.addInline("logo-icon", new ClassPathResource("static/images/logo-icon.png"));
            helper.addInline("logo-text", new ClassPathResource("static/images/logo-text.png"));
        } catch (MessagingException e) {

        }
        mailSender.send(message); // 이메일 발송
    }

    public TokenVerificationResponse verifyToken(String token) {
        try {
            if (jwtUtil.ieExpired(token)) {
                throw new ApiException(TOKEN_EXPIRED);
            }
        } catch (ExpiredJwtException e) {
            throw new ApiException(TOKEN_EXPIRED);
        }

        String email = jwtUtil.getEmail(token);
        if (!userRepository.existsByEmail(email)) {
            throw new ApiException(USER_NOT_FOUND);
        }
        return TokenVerificationResponse.builder()
            .email(email)
            .build();
    }
}
