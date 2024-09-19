package kr.codeit.relaxtogether.entity.gathering;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import kr.codeit.relaxtogether.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GatheringTest {

    @DisplayName("유저와 모임의 매핑이 정상적으로 진행된다.")
    @Test
    void createUserGathering_Success() {
        // given
        User user = User.builder()
            .email("test@gmail.com")
            .name("testuser")
            .companyName("testcompany")
            .password("testpassword")
            .build();

        Gathering gathering = Gathering.builder()
            .name("Test Gathering")
            .location(Location.HONGDAE)
            .imageUrl("test_image_url")
            .type(Type.WORKATION)
            .dateTime(LocalDateTime.now().plusDays(1))
            .registrationEnd(LocalDateTime.now().plusDays(1))
            .capacity(10)
            .build();

        // when
        UserGathering userGathering = UserGathering.builder()
            .user(user)
            .gathering(gathering)
            .build();

        // then
        assertThat(userGathering.getUser()).isEqualTo(user);
        assertThat(userGathering.getGathering()).isEqualTo(gathering);
    }
}