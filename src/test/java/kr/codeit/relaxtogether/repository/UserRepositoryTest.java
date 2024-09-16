package kr.codeit.relaxtogether.repository;

import static org.assertj.core.api.Assertions.assertThat;

import kr.codeit.relaxtogether.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @DisplayName("해당 이메일을 갖고 있는 유저가 존재할 경우 true를 반환합니다.")
    @Test
    void successExistsByEmail() {
        // given
        String email = "test@test.com";
        User user = User.builder()
            .email(email)
            .build();

        userRepository.save(user);

        // when
        boolean result = userRepository.existsByEmail(email);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("해당 이메일을 갖고 있는 유저가 존재하지 않을 경우 false를 반환합니다.")
    @Test
    void failExistsByEmail() {
        // given
        String email = "test1@test.com";
        String emailToCheck = "test2@test.com";
        User user = User.builder()
            .email(email)
            .build();

        userRepository.save(user);

        // when
        boolean result = userRepository.existsByEmail(emailToCheck);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("이메일로 유저 객체를 조회합니다.")
    @Test
    void findByEmail() {
        // given
        User user = User.builder()
            .email("test@test.com")
            .build();

        userRepository.save(user);

        // when
        User findUser = userRepository.findByEmail("test@test.com").get();

        // then
        assertThat(findUser.getEmail()).isEqualTo("test@test.com");
    }
}
