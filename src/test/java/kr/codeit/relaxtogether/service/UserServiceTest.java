package kr.codeit.relaxtogether.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import kr.codeit.relaxtogether.dto.user.request.EmailCheckRequest;
import kr.codeit.relaxtogether.dto.user.request.JoinUserRequest;
import kr.codeit.relaxtogether.dto.user.request.UpdateUserRequest;
import kr.codeit.relaxtogether.dto.user.response.UserDetailsResponse;
import kr.codeit.relaxtogether.entity.User;
import kr.codeit.relaxtogether.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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

    @DisplayName("회원가입 시 유저 정보가 DB에 저장됩니다.")
    @Test
    void signup() {
        // given
        JoinUserRequest request = JoinUserRequest.builder()
            .email("test@test.com")
            .password("password")
            .name("name")
            .companyName("companyName")
            .build();

        // when
        userService.signup(request);

        // then
        assertThat(userRepository.findAll()).hasSize(1)
            .extracting("email", "name", "companyName")
            .containsExactlyInAnyOrder(
                tuple("test@test.com", "name", "companyName")
            );
    }

    @DisplayName("유저 정보를 업데이트합니다.")
    @Test
    void update() {
        // given
        String email = "test@test.com";
        User user = User.builder()
            .email(email)
            .companyName("before")
            .build();

        userRepository.save(user);

        UpdateUserRequest request = UpdateUserRequest.builder()
            .companyName("after")
            .profileImage(null)
            .build();

        // when
        userService.update(request, email);

        // then
        assertThat(userRepository.findByEmail(email).get().getCompanyName()).isEqualTo("after");
    }

    @DisplayName("이메일을 통해 해당 유저의 정보를 가져옵니다.")
    @Test
    void getUserDetails() {
        // given
        String email = "test@test.com";
        String name = "name";
        String companyName = "com";
        User user = User.builder()
            .email(email)
            .name(name)
            .companyName(companyName)
            .build();

        userRepository.save(user);

        // when
        UserDetailsResponse userDetails = userService.getUserDetails(email);

        // then
        assertThat(userDetails.getEmail()).isEqualTo(email);
        assertThat(userDetails.getName()).isEqualTo(name);
        assertThat(userDetails.getCompanyName()).isEqualTo(companyName);
    }
}
