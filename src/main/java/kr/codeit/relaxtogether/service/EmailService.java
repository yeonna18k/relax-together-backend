package kr.codeit.relaxtogether.service;

import static kr.codeit.relaxtogether.exception.ErrorCode.TOKEN_EXPIRED;
import static kr.codeit.relaxtogether.exception.ErrorCode.USER_NOT_FOUND;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import kr.codeit.relaxtogether.auth.jwt.JwtUtil;
import kr.codeit.relaxtogether.exception.ApiException;
import kr.codeit.relaxtogether.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final JavaMailSender javaMailSender;

    public void sendEmail(String email) {
        // 해당 email이 존재하는지 확인
        if (!userRepository.existsByEmail(email)) {
            throw new ApiException(USER_NOT_FOUND);
        }

        // 링크 검증을 위한 토큰 추가
        MimeMessage message = javaMailSender.createMimeMessage();
        String token = jwtUtil.createEmailVerificationToken(email);
        try {
            message.setRecipients(RecipientType.TO, email); // 수신자 이메일
            message.setSubject("같이달램 비밀번호 변경"); // 이메일 제목
            String content = "";
            content += "<h3>" + "해당 링크를 클릭해서 비밀번호 변경을 진행해주세요." + "</h3>";
            content +=
                "<h1><a href=" + "'http://localhost:8080/api/verify-link?token=" + token + "'>비밀번호 변경 링크</a></h1>";
            message.setText(content, "UTF-8", "html"); // 이메일 내용
        } catch (MessagingException e) {

        }
        javaMailSender.send(message); // 이메일 발송
    }

    public boolean verifyLink(String token) {
        try {
            if (jwtUtil.ieExpired(token)) {
                throw new ApiException(TOKEN_EXPIRED);
            }
        } catch (ExpiredJwtException e) {
            throw new ApiException(TOKEN_EXPIRED);
        }

        if (!userRepository.existsByEmail(jwtUtil.getEmail(token))) {
            throw new ApiException(USER_NOT_FOUND);
        }
        return true;
    }
}
