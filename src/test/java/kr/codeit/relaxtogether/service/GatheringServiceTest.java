package kr.codeit.relaxtogether.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import kr.codeit.relaxtogether.dto.gathering.request.CreateGatheringRequest;
import kr.codeit.relaxtogether.dto.gathering.response.GatheringDetailResponse;
import kr.codeit.relaxtogether.entity.User;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
import kr.codeit.relaxtogether.entity.gathering.Location;
import kr.codeit.relaxtogether.entity.gathering.Type;
import kr.codeit.relaxtogether.entity.gathering.UserGathering;
import kr.codeit.relaxtogether.repository.UserGatheringRepository;
import kr.codeit.relaxtogether.repository.UserRepository;
import kr.codeit.relaxtogether.repository.gathering.GatheringRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class GatheringServiceTest {

    @Autowired
    private GatheringService gatheringService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GatheringRepository gatheringRepository;

    @Autowired
    private UserGatheringRepository userGatheringRepository;

    @Test
    @DisplayName("정상적인 모임과 사용자 모임이 생성되어야 한다")
    void createGathering() {
        // Given
        CreateGatheringRequest request = CreateGatheringRequest.builder()
            .name(null)
            .location(Location.HONGDAE.getText())
            .type(Type.MINDFULNESS.getText())
            .dateTime(LocalDateTime.now().plusDays(10))
            .registrationEnd(LocalDateTime.now().plusDays(5))
            .capacity(10)
            .imageUrl("https://example.com/image.png")
            .build();

        User user = userRepository.save(
            User.builder()
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .companyName("Test Company")
                .build()
        );

        // When
        gatheringService.createGathering(request, user.getEmail());

        // Then
        Gathering savedGathering = gatheringRepository.findAll().get(0);
        assertThat(savedGathering).isNotNull();
        assertThat(savedGathering.getName()).isNull();
        assertThat(savedGathering.getCreatedBy().getEmail()).isEqualTo(user.getEmail());

        UserGathering savedUserGathering = userGatheringRepository.findAll().get(0);
        assertThat(savedUserGathering).isNotNull();
        assertThat(savedUserGathering.getUser().getEmail()).isEqualTo(user.getEmail());
        assertThat(savedUserGathering.getGathering().getName()).isEqualTo(request.getName());
    }

    @Test
    @DisplayName("모집 종료일이 모임 시작일 이후일 경우 예외가 발생해야 한다")
    void createGathering_ThrowExceptionIfRegistrationEndIsAfterDateTime() {
        // Given
        CreateGatheringRequest request = CreateGatheringRequest.builder()
            .name("Test Gathering")
            .location(Location.KONDAE.getText())
            .type(Type.WORKATION.getText())
            .dateTime(LocalDateTime.now().plusDays(5))
            .registrationEnd(LocalDateTime.now().plusDays(10))
            .capacity(10)
            .imageUrl("https://example.com/image.png")
            .build();

        User user = userRepository.save(
            User.builder()
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .companyName("Test Company")
                .build()
        );

        // When/Then
        assertThatThrownBy(() -> gatheringService.createGathering(request, user.getEmail()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("모집 종료일은 모임 시작일 이전이어야 합니다.");
    }

    @Test
    @DisplayName("유저가 존재하지 않을 경우 예외가 발생해야 한다")
    void createGathering_ThrowExceptionIfUserNotFound() {
        // Given
        CreateGatheringRequest request = CreateGatheringRequest.builder()
            .name("Test Gathering")
            .location(Location.SINRIM.getText())
            .type(Type.WORKATION.getText())
            .dateTime(LocalDateTime.now().plusDays(10))
            .registrationEnd(LocalDateTime.now().plusDays(5))
            .capacity(10)
            .imageUrl("https://example.com/image.png")
            .build();

        // When/Then
        assertThatThrownBy(() -> gatheringService.createGathering(request, "nonexistent@example.com"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유저정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("모임 상세 조회를 할 수 있다")
    void getGatheringDetail_ReturnGatheringDetail() {
        // Given
        User user = userRepository.save(
            User.builder()
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .companyName("Test Company")
                .build()
        );

        Gathering gathering = gatheringRepository.save(
            Gathering.builder()
                .createdBy(user)
                .name("Test Gathering")
                .location(Location.KONDAE)
                .type(Type.OFFICE_STRETCHING)
                .dateTime(LocalDateTime.now().plusDays(5))
                .registrationEnd(LocalDateTime.now().plusDays(3))
                .capacity(10)
                .imageUrl("https://example.com/image.png")
                .build()
        );

        userGatheringRepository.save(
            UserGathering.builder()
                .user(user)
                .gathering(gathering)
                .build());

        // When
        GatheringDetailResponse response = gatheringService.getGatheringDetail(gathering.getId());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(gathering.getId());
        assertThat(response.getName()).isEqualTo(gathering.getName());
        assertThat(response.getCreatedBy()).isEqualTo(user.getId());
        assertThat(response.getLocation()).isEqualTo(gathering.getLocation().getText());
        assertThat(response.getType()).isEqualTo(gathering.getType().getText());
        assertThat(response.getCapacity()).isEqualTo(gathering.getCapacity());
        assertThat(response.getParticipantCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 모임 상세 조회 시 예외가 발생한다")
    void getGatheringDetail_ThrowExceptionWhenGatheringNotFound() {
        // Given
        Long invalidGatheringId = 999L;

        // When / Then
        assertThatThrownBy(() -> gatheringService.getGatheringDetail(invalidGatheringId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("해당 모임을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("정상적인 모임 참여가 되어야 한다")
    void joinGathering_Success() {
        // Given
        User user = userRepository.save(
            User.builder()
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .companyName("Test Company")
                .build()
        );

        Gathering gathering = gatheringRepository.save(
            Gathering.builder()
                .createdBy(user)
                .name("Test Gathering")
                .location(Location.SINRIM)
                .type(Type.MINDFULNESS)
                .dateTime(LocalDateTime.now().plusDays(10))
                .registrationEnd(LocalDateTime.now().plusDays(5))
                .capacity(10)
                .build()
        );

        // When
        gatheringService.joinGathering(gathering.getId(), user.getEmail());

        // Then
        UserGathering userGathering = userGatheringRepository.findAll().get(0);
        assertThat(userGathering).isNotNull();
        assertThat(userGathering.getUser().getEmail()).isEqualTo(user.getEmail());
        assertThat(userGathering.getGathering().getId()).isEqualTo(gathering.getId());
    }

    @Test
    @DisplayName("모임 정원이 초과되었을 때 예외가 발생해야 한다")
    void joinGathering_ThrowExceptionIfCapacityExceeded() {
        // Given
        User user = userRepository.save(
            User.builder()
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .companyName("Test Company")
                .build()
        );

        Gathering gathering = gatheringRepository.save(
            Gathering.builder()
                .createdBy(user)
                .name("Test Gathering")
                .location(Location.KONDAE)
                .type(Type.WORKATION)
                .dateTime(LocalDateTime.now().plusDays(10))
                .registrationEnd(LocalDateTime.now().plusDays(5))
                .capacity(1) // 정원이 1명으로 설정됨
                .build()
        );

        gatheringService.joinGathering(gathering.getId(), user.getEmail());

        User anotherUser = userRepository.save(
            User.builder()
                .email("another@example.com")
                .password("password")
                .name("Another User")
                .companyName("Another Company")
                .build()
        );

        // When/Then
        assertThatThrownBy(() -> gatheringService.joinGathering(gathering.getId(), anotherUser.getEmail()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("해당 모임은 이미 정원이 찼습니다.");
    }

    @Test
    @DisplayName("이미 모임에 참여한 경우 예외가 발생해야 한다")
    void joinGathering_ThrowExceptionIfAlreadyJoined() {
        // Given
        User user = userRepository.save(
            User.builder()
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .companyName("Test Company")
                .build()
        );

        Gathering gathering = gatheringRepository.save(
            Gathering.builder()
                .createdBy(user)
                .name("Test Gathering")
                .location(Location.SINRIM)
                .type(Type.MINDFULNESS)
                .dateTime(LocalDateTime.now().plusDays(10))
                .registrationEnd(LocalDateTime.now().plusDays(5))
                .capacity(10)
                .build()
        );

        gatheringService.joinGathering(gathering.getId(), user.getEmail());

        // When/Then
        assertThatThrownBy(() -> gatheringService.joinGathering(gathering.getId(), user.getEmail()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("이미 참여한 모임입니다.");
    }

    @Test
    @DisplayName("유저가 존재하지 않을 경우 예외가 발생해야 한다")
    void joinGathering_ThrowExceptionIfUserNotFound() {
        // Given
        Gathering gathering = gatheringRepository.save(
            Gathering.builder()
                .createdBy(null)
                .name("Test Gathering")
                .location(Location.SINRIM)
                .type(Type.WORKATION)
                .dateTime(LocalDateTime.now().plusDays(10))
                .registrationEnd(LocalDateTime.now().plusDays(5))
                .capacity(10)
                .build()
        );

        // When/Then
        assertThatThrownBy(() -> gatheringService.joinGathering(gathering.getId(), "nonexistent@example.com"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유저정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("모임이 존재하지 않을 경우 예외가 발생해야 한다")
    void joinGathering_ThrowExceptionIfGatheringNotFound() {
        // Given
        User user = userRepository.save(
            User.builder()
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .companyName("Test Company")
                .build()
        );

        // When/Then
        assertThatThrownBy(() -> gatheringService.joinGathering(999L, user.getEmail()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("해당 모임을 찾을 수 없습니다.");
    }
}