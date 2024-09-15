package kr.codeit.relaxtogether.service;

import static org.assertj.core.api.Assertions.assertThat;

import kr.codeit.relaxtogether.dto.user.request.EmailCheckRequest;
import kr.codeit.relaxtogether.entity.User;
import kr.codeit.relaxtogether.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("해당 이메일을 갖고 있는 유저가 존재할 경우 true를 반환합니다.")
    @Test
    void successCheckEmail() {
        // given
        String email = "test@test.com";
        User user = User.builder()
            .email(email)
            .build();

        userRepository.save(user);

        EmailCheckRequest request = EmailCheckRequest.builder()
            .email(email)
            .build();

        // when
        boolean result = userService.checkEmail(request);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("해당 이메일을 갖고 있는 유저가 존재하지 않을 경우 false를 반환합니다.")
    @Test
    void failCheckEmail() {
        // given
        String email = "test1@test.com";
        String emailToCheck = "test2@test.com";
        User user = User.builder()
            .email(email)
            .build();

        userRepository.save(user);

        EmailCheckRequest request = EmailCheckRequest.builder()
            .email(emailToCheck)
            .build();

        // when
        boolean result = userService.checkEmail(request);

        // then
        assertThat(result).isFalse();
    }
}
