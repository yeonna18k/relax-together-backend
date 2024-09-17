package kr.codeit.relaxtogether.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class UserTest {

    @DisplayName("유저 정보를 업데이트합니다.")
    @Test
    void updateUser() {
        // given
        User user = User.builder()
            .companyName("companyName")
            .build();

        // when
        user.update("new companyName", "profileImage");

        // then
        assertThat(user.getCompanyName()).isEqualTo("new companyName");
        assertThat(user.getProfileImage()).isEqualTo("profileImage");
    }
}
