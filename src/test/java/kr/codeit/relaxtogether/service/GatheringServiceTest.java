package kr.codeit.relaxtogether.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.ZonedDateTime;
import kr.codeit.relaxtogether.dto.PagedResponse;
import kr.codeit.relaxtogether.dto.gathering.request.CreateGatheringRequest;
import kr.codeit.relaxtogether.dto.gathering.response.GatheringDetailResponse;
import kr.codeit.relaxtogether.dto.gathering.response.HostedGatheringResponse;
import kr.codeit.relaxtogether.dto.gathering.response.MyGatheringResponse;
import kr.codeit.relaxtogether.dto.gathering.response.Participant;
import kr.codeit.relaxtogether.dto.gathering.response.ParticipantsResponse;
import kr.codeit.relaxtogether.entity.Review;
import kr.codeit.relaxtogether.entity.User;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
import kr.codeit.relaxtogether.entity.gathering.Location;
import kr.codeit.relaxtogether.entity.gathering.Status;
import kr.codeit.relaxtogether.entity.gathering.Type;
import kr.codeit.relaxtogether.entity.gathering.UserGathering;
import kr.codeit.relaxtogether.exception.ApiException;
import kr.codeit.relaxtogether.repository.UserGatheringRepository;
import kr.codeit.relaxtogether.repository.UserRepository;
import kr.codeit.relaxtogether.repository.gathering.GatheringRepository;
import kr.codeit.relaxtogether.repository.review.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("정상적인 모임과 사용자 모임이 생성되어야 한다")
    void createGathering() {
        // Given
        CreateGatheringRequest request = CreateGatheringRequest.builder()
            .name(null)
            .location(Location.HONGDAE.getText())
            .type(Type.MINDFULNESS.getText())
            .dateTime(ZonedDateTime.now().plusDays(10))
            .registrationEnd(ZonedDateTime.now().plusDays(5))
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
        assertThat(savedGathering.getHostUser().getEmail()).isEqualTo(user.getEmail());

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
            .dateTime(ZonedDateTime.now().plusDays(5))
            .registrationEnd(ZonedDateTime.now().plusDays(10))
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
            .isInstanceOf(ApiException.class)
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
            .dateTime(ZonedDateTime.now().plusDays(10))
            .registrationEnd(ZonedDateTime.now().plusDays(5))
            .capacity(10)
            .imageUrl("https://example.com/image.png")
            .build();

        // When/Then
        assertThatThrownBy(() -> gatheringService.createGathering(request, "nonexistent@example.com"))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining("해당 유저를 찾을 수 없습니다. 로그인 정보를 확인해 주세요.");
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
                .hostUser(user)
                .name("Test Gathering")
                .location(Location.KONDAE)
                .type(Type.OFFICE_STRETCHING)
                .dateTime(ZonedDateTime.now().plusDays(5))
                .registrationEnd(ZonedDateTime.now().plusDays(3))
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
        assertThat(response.getHostUser()).isEqualTo(user.getId());
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
            .isInstanceOf(ApiException.class)
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
                .hostUser(user)
                .name("Test Gathering")
                .location(Location.SINRIM)
                .type(Type.MINDFULNESS)
                .dateTime(ZonedDateTime.now().plusDays(10))
                .registrationEnd(ZonedDateTime.now().plusDays(5))
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
                .hostUser(user)
                .name("Test Gathering")
                .location(Location.KONDAE)
                .type(Type.WORKATION)
                .dateTime(ZonedDateTime.now().plusDays(10))
                .registrationEnd(ZonedDateTime.now().plusDays(5))
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
            .isInstanceOf(ApiException.class)
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
                .hostUser(user)
                .name("Test Gathering")
                .location(Location.SINRIM)
                .type(Type.MINDFULNESS)
                .dateTime(ZonedDateTime.now().plusDays(10))
                .registrationEnd(ZonedDateTime.now().plusDays(5))
                .capacity(10)
                .build()
        );

        gatheringService.joinGathering(gathering.getId(), user.getEmail());

        // When/Then
        assertThatThrownBy(() -> gatheringService.joinGathering(gathering.getId(), user.getEmail()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining("이미 참여한 모임입니다.");
    }

    @Test
    @DisplayName("유저가 존재하지 않을 경우 예외가 발생해야 한다")
    void joinGathering_ThrowExceptionIfUserNotFound() {
        // Given
        Gathering gathering = gatheringRepository.save(
            Gathering.builder()
                .hostUser(null)
                .name("Test Gathering")
                .location(Location.SINRIM)
                .type(Type.WORKATION)
                .dateTime(ZonedDateTime.now().plusDays(10))
                .registrationEnd(ZonedDateTime.now().plusDays(5))
                .capacity(10)
                .build()
        );

        // When/Then
        assertThatThrownBy(() -> gatheringService.joinGathering(gathering.getId(), "nonexistent@example.com"))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining("해당 유저를 찾을 수 없습니다. 로그인 정보를 확인해 주세요.");
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
            .isInstanceOf(ApiException.class)
            .hasMessageContaining("해당 모임을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("주최자가 모임을 정상적으로 취소한다")
    void cancelGathering_cancelGatheringAndRemoveHostUserGathering() {
        // Given
        User hostUser = userRepository.save(User.builder()
            .email("host@example.com")
            .password("password")
            .name("Host User")
            .build());

        Gathering gathering = gatheringRepository.save(Gathering.builder()
            .hostUser(hostUser)
            .name("Test Gathering")
            .location(Location.KONDAE)
            .type(Type.OFFICE_STRETCHING)
            .dateTime(ZonedDateTime.now().plusDays(1))
            .registrationEnd(ZonedDateTime.now().plusDays(1))
            .capacity(10)
            .build());

        userGatheringRepository.save(UserGathering.builder()
            .user(hostUser)
            .gathering(gathering)
            .build());

        // When
        gatheringService.cancelGathering(gathering.getId(), hostUser.getEmail());

        // Then
        Gathering cancelledGathering = gatheringRepository.findById(gathering.getId())
            .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다."));

        assertThat(cancelledGathering.getStatus()).isEqualTo(Status.CANCELLED);
        assertThat(userGatheringRepository.existsByUserIdAndGatheringId(hostUser.getId(), gathering.getId())).isFalse();
    }

    @Test
    @DisplayName("비주최자가 모임을 취소하려고 하면 예외가 발생한다")
    void cancelGathering_throwExceptionWhenNonHostUserTriesToCancel() {
        // Given
        User hostUser = userRepository.save(User.builder()
            .email("host@example.com")
            .password("password")
            .name("Host User")
            .build());

        User nonHostUser = userRepository.save(User.builder()
            .email("nonhost@example.com")
            .password("password")
            .name("Non-host User")
            .build());

        Gathering gathering = gatheringRepository.save(Gathering.builder()
            .hostUser(hostUser)
            .name("Test Gathering")
            .location(Location.KONDAE)
            .type(Type.OFFICE_STRETCHING)
            .dateTime(ZonedDateTime.now().plusDays(1))
            .registrationEnd(ZonedDateTime.now().plusDays(1))
            .capacity(10)
            .build());

        // When / Then
        assertThatThrownBy(() -> gatheringService.cancelGathering(gathering.getId(), nonHostUser.getEmail()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining("해당 요청에 대한 권한이 없습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 모임을 취소하려고 하면 예외가 발생한다")
    void cancelGathering_throwExceptionWhenGatheringDoesNotExist() {
        // Given
        User hostUser = userRepository.save(User.builder()
            .email("host@example.com")
            .password("password")
            .name("Host User")
            .build());

        Long invalidGatheringId = 999L;

        // When / Then
        assertThatThrownBy(() -> gatheringService.cancelGathering(invalidGatheringId, hostUser.getEmail()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining("해당 요청에 대한 권한이 없습니다.");
    }

    @Test
    @DisplayName("정상적으로 모임 참여를 취소한다")
    void leaveGathering() {
        // Given
        User hostUser = userRepository.save(User.builder()
            .email("user@example.com")
            .password("password")
            .name("Host User")
            .build());

        User user = userRepository.save(User.builder()
            .email("user1@example.com")
            .password("password")
            .name("Test User")
            .build());

        Gathering gathering = gatheringRepository.save(Gathering.builder()
            .hostUser(hostUser)
            .name("Test Gathering")
            .location(Location.KONDAE)
            .type(Type.OFFICE_STRETCHING)
            .dateTime(ZonedDateTime.now().plusDays(1))
            .registrationEnd(ZonedDateTime.now().plusDays(1))
            .capacity(10)
            .build());

        userGatheringRepository.save(UserGathering.builder()
            .user(hostUser)
            .gathering(gathering)
            .build());

        userGatheringRepository.save(UserGathering.builder()
            .user(user)
            .gathering(gathering)
            .build());

        // When
        gatheringService.leaveGathering(gathering.getId(), user.getEmail());

        // Then
        assertThat(userGatheringRepository.existsByUserIdAndGatheringId(user.getId(), gathering.getId())).isFalse();
    }

    @Test
    @DisplayName("이미 지난 모임에 대한 참여 취소 시 예외 발생")
    void leaveGathering_throwException() {
        // Given
        User user = userRepository.save(User.builder()
            .email("user@example.com")
            .password("password")
            .name("Test User")
            .build());

        Gathering pastGathering = gatheringRepository.save(Gathering.builder()
            .hostUser(user)
            .name("Past Gathering")
            .location(Location.KONDAE)
            .type(Type.OFFICE_STRETCHING)
            .dateTime(ZonedDateTime.now().minusDays(1))
            .registrationEnd(ZonedDateTime.now().minusDays(2))
            .capacity(10)
            .build());

        userGatheringRepository.save(UserGathering.builder()
            .user(user)
            .gathering(pastGathering)
            .build());

        // When / Then
        assertThatThrownBy(() -> gatheringService.leaveGathering(pastGathering.getId(), user.getEmail()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining("주최자는 모임 참여 취소가 불가능합니다.");
    }

    @Test
    @DisplayName("참여하지 않은 모임을 취소하려는 경우 예외 발생")
    void leaveGathering_throwExceptionIfUserDidNotJoin() {
        // Given
        User hostUser = userRepository.save(User.builder()
            .email("user@example.com")
            .password("password")
            .name("Host User")
            .build());

        User user = userRepository.save(User.builder()
            .email("user1@example.com")
            .password("password")
            .name("Test User")
            .build());

        Gathering gathering = gatheringRepository.save(Gathering.builder()
            .hostUser(hostUser)
            .name("Test Gathering")
            .location(Location.KONDAE)
            .type(Type.OFFICE_STRETCHING)
            .dateTime(ZonedDateTime.now().plusDays(1))
            .registrationEnd(ZonedDateTime.now().plusDays(1))
            .capacity(10)
            .build());

        // When / Then
        assertThatThrownBy(() -> gatheringService.leaveGathering(gathering.getId(), user.getEmail()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining("참여하지 않은 모임입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 모임에 대한 참여 취소 시 예외 발생")
    void leaveGathering_throwExceptionIfGatheringDoesNotExist() {
        // Given
        User user = userRepository.save(User.builder()
            .email("user@example.com")
            .password("password")
            .name("Test User")
            .build());

        Long nonExistentGatheringId = 999L;

        // When / Then
        assertThatThrownBy(() -> gatheringService.leaveGathering(nonExistentGatheringId, user.getEmail()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining("모임을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("정상적인 모임 참가자 목록 조회")
    void shouldReturnParticipants() {
        // Given
        User user1 = createUser("user1@example.com", "User One");
        User user2 = createUser("user2@example.com", "User Two");

        Gathering gathering = createGathering(user1, "Test Gathering");

        addUserToGathering(user1, gathering);
        addUserToGathering(user2, gathering);

        // When
        Pageable pageable = PageRequest.of(0, 10);
        ParticipantsResponse participantsResponse = gatheringService.getParticipants(gathering.getId(), pageable);

        // Then
        assertThat(participantsResponse.getParticipants()).hasSize(2);
        assertThat(participantsResponse.getParticipants())
            .extracting(Participant::getName)
            .containsExactlyInAnyOrder("User One", "User Two");

        assertThat(participantsResponse.getCurrentPage()).isZero();
        assertThat(participantsResponse.getTotalPages()).isEqualTo(1);
        assertThat(participantsResponse.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("참가자가 없는 모임 조회")
    void shouldReturnEmptyListForNoParticipants() {
        // Given
        User user = createUser("host@example.com", "Host User");
        Gathering gathering = createGathering(user, "Empty Gathering");

        // When
        Pageable pageable = PageRequest.of(0, 10);
        ParticipantsResponse participantsResponse = gatheringService.getParticipants(gathering.getId(), pageable);

        // Then
        assertThat(participantsResponse.getParticipants()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 모임 조회 시 예외 발생")
    void shouldThrowExceptionForNonExistentGathering() {
        // Given
        Long nonExistentGatheringId = 999L;

        // When / Then
        Pageable pageable = PageRequest.of(0, 10);
        assertThatThrownBy(() -> gatheringService.getParticipants(nonExistentGatheringId, pageable))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining("해당 모임을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("페이지네이션 적용 확인")
    void shouldApplyPagination() {
        // Given
        User user1 = createUser("user1@example.com", "User One");
        User user2 = createUser("user2@example.com", "User Two");
        User user3 = createUser("user3@example.com", "User Three");

        Gathering gathering = createGathering(user1, "Paged Gathering");

        addUserToGathering(user1, gathering);
        addUserToGathering(user2, gathering);
        addUserToGathering(user3, gathering);

        // When
        Pageable pageable = PageRequest.of(0, 2);
        ParticipantsResponse participantsResponse = gatheringService.getParticipants(gathering.getId(), pageable);

        // Then
        assertThat(participantsResponse.getParticipants()).hasSize(2);
        assertThat(participantsResponse.getParticipants())
            .extracting(Participant::getName)
            .containsExactlyInAnyOrder("User One", "User Two");
    }

    @Test
    @DisplayName("정상적으로 내가 주최한 모임 목록을 조회할 수 있다")
    void getMyHostedGatherings_Success() {
        // Given
        User user = userRepository.save(
            User.builder()
                .email("host@example.com")
                .password("password")
                .name("Host User")
                .build()
        );

        gatheringRepository.save(
            Gathering.builder()
                .hostUser(user)
                .name("Gathering 1")
                .location(Location.KONDAE)
                .type(Type.OFFICE_STRETCHING)
                .dateTime(ZonedDateTime.now().plusDays(5))
                .registrationEnd(ZonedDateTime.now().plusDays(3))
                .capacity(10)
                .imageUrl("https://example.com/image1.png")
                .build()
        );

        gatheringRepository.save(
            Gathering.builder()
                .hostUser(user)
                .name("Gathering 2")
                .location(Location.HONGDAE)
                .type(Type.MINDFULNESS)
                .dateTime(ZonedDateTime.now().plusDays(10))
                .registrationEnd(ZonedDateTime.now().plusDays(7))
                .capacity(20)
                .imageUrl("https://example.com/image2.png")
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());

        // When
        PagedResponse<HostedGatheringResponse> response = gatheringService.getMyHostedGatherings(user.getEmail(),
            pageable);

        // Then
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().get(0).getName()).isEqualTo("Gathering 2");
        assertThat(response.getContent().get(1).getName()).isEqualTo("Gathering 1");
        assertThat(response.isHasNext()).isFalse();
    }

    @Test
    @DisplayName("내가 주최한 모임이 없을 경우 빈 목록을 반환해야 한다")
    void getMyHostedGatherings_EmptyList() {
        // Given
        User user = userRepository.save(
            User.builder()
                .email("host@example.com")
                .password("password")
                .name("Host User")
                .build()
        );

        Pageable pageable = PageRequest.of(0, 10);

        // When
        PagedResponse<HostedGatheringResponse> response = gatheringService.getMyHostedGatherings(user.getEmail(),
            pageable);

        // Then
        assertThat(response.getContent()).isEmpty();
        assertThat(response.isHasNext()).isFalse();
    }

    @Test
    @DisplayName("페이징이 적용된 내가 주최한 모임 목록을 조회할 수 있다")
    void getMyHostedGatherings_WithPaging() {
        // Given
        User user = userRepository.save(
            User.builder()
                .email("host@example.com")
                .password("password")
                .name("Host User")
                .build()
        );

        for (int i = 1; i <= 5; i++) {
            gatheringRepository.save(
                Gathering.builder()
                    .hostUser(user)
                    .name("Gathering " + i)
                    .location(Location.HONGDAE)
                    .type(Type.MINDFULNESS)
                    .dateTime(ZonedDateTime.now().plusDays(i))
                    .registrationEnd(ZonedDateTime.now().plusDays(i - 1))
                    .capacity(20)
                    .imageUrl("https://example.com/image" + i + ".png")
                    .build()
            );
        }

        Pageable pageable = PageRequest.of(0, 2, Sort.by("createdDate").descending());

        // When
        PagedResponse<HostedGatheringResponse> response = gatheringService.getMyHostedGatherings(user.getEmail(),
            pageable);

        // Then
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().get(0).getName()).isEqualTo("Gathering 5");
        assertThat(response.getContent().get(1).getName()).isEqualTo("Gathering 4");
        assertThat(response.isHasNext()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 유저로 내가 주최한 모임을 조회할 경우 예외 발생")
    void getMyHostedGatherings_UserNotFound() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When/Then
        assertThatThrownBy(() -> gatheringService.getMyHostedGatherings("nonexistent@example.com", pageable))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining("해당 유저를 찾을 수 없습니다. 로그인 정보를 확인해 주세요.");
    }

    @Test
    @DisplayName("모임이 이미 종료된 경우 isCompleted 값이 true로 반환된다")
    void getMyGatherings_isCompleted() {
        // Given
        User user1 = createUser("user@example.com", "User");
        User user2 = createUser("host@example.com", "Host User");

        Gathering pastGathering = gatheringRepository.save(
            Gathering.builder()
                .hostUser(user2)
                .name("Past Gathering")
                .location(Location.HONGDAE)
                .type(Type.MINDFULNESS)
                .dateTime(ZonedDateTime.now().minusDays(10))
                .registrationEnd(ZonedDateTime.now().minusDays(11))
                .capacity(10)
                .imageUrl("https://example.com/image.png")
                .build());

        userGatheringRepository.save(UserGathering.builder()
            .user(user1)
            .gathering(pastGathering)
            .build());

        userGatheringRepository.save(UserGathering.builder()
            .user(user2)
            .gathering(pastGathering)
            .build());

        PageRequest pageable = PageRequest.of(0, 5);

        // When
        PagedResponse<MyGatheringResponse> response = gatheringService.getMyGatherings(user1.getEmail(), pageable);

        // Then
        boolean isCompleted = response.getContent().stream()
            .anyMatch(gathering -> gathering.getName().equals("Past Gathering") && gathering.isCompleted());

        assertThat(isCompleted).isTrue();
    }

    @Test
    @DisplayName("취소된 모임의 상태를 확인한다")
    void getMyGatherings_ReturnCancelledIfCancelled() {
        // Given
        User user1 = createUser("test1@example.com", "Test User1");
        User user2 = createUser("test2@example.com", "Test User2");

        Gathering ongoingGathering = gatheringRepository.save(
            Gathering.builder()
                .hostUser(user2)
                .name("ONGOING Gathering")
                .location(Location.HONGDAE)
                .type(Type.MINDFULNESS)
                .dateTime(ZonedDateTime.now().minusDays(5))
                .registrationEnd(ZonedDateTime.now().minusDays(11))
                .capacity(10)
                .imageUrl("https://example.com/image.png")
                .build());

        Gathering cancelledGathering = gatheringRepository.save(
            Gathering.builder()
                .hostUser(user2)
                .name("CANCELLED Gathering")
                .location(Location.HONGDAE)
                .type(Type.MINDFULNESS)
                .dateTime(ZonedDateTime.now().minusDays(10))
                .registrationEnd(ZonedDateTime.now().minusDays(11))
                .capacity(10)
                .imageUrl("https://example.com/image.png")
                .build());
        cancelledGathering.cancel();

        addUserToGathering(user2, ongoingGathering);
        addUserToGathering(user2, cancelledGathering);
        addUserToGathering(user1, ongoingGathering);
        addUserToGathering(user1, cancelledGathering);

        // When
        PageRequest pageable = PageRequest.of(0, 10);
        PagedResponse<MyGatheringResponse> response = gatheringService.getMyGatherings(user1.getEmail(), pageable);

        // Then
        assertThat(response.getContent()).hasSize(2);

        MyGatheringResponse ongoingResponse = response.getContent().stream()
            .filter(g -> g.getName().equals("ONGOING Gathering"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Ongoing Gathering not found"));

        MyGatheringResponse cancelledResponse = response.getContent().stream()
            .filter(g -> g.getName().equals("CANCELLED Gathering"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Cancelled Gathering not found"));

        assertThat(ongoingResponse.getStatus()).isEqualTo(Status.ONGOING);
        assertThat(ongoingResponse.isCompleted()).isTrue();

        assertThat(cancelledResponse.getStatus()).isEqualTo(Status.CANCELLED);
        assertThat(cancelledResponse.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("참여한 모임 중 주최한 모임은 제외하고 조회")
    void getMyGatherings_excludeHostedGatherings() {
        // Given
        User user = userRepository.save(User.builder()
            .email("user@example.com")
            .password("password")
            .name("Test User")
            .build());

        User hostUser = userRepository.save(User.builder()
            .email("host@example.com")
            .password("password")
            .name("Host User")
            .build());

        Gathering gathering1 = gatheringRepository.save(Gathering.builder()
            .hostUser(hostUser) // 다른 사용자가 주최한 모임
            .name("Other Gathering")
            .location(Location.KONDAE)
            .type(Type.OFFICE_STRETCHING)
            .dateTime(ZonedDateTime.now().plusDays(1))
            .registrationEnd(ZonedDateTime.now().plusDays(1))
            .capacity(10)
            .build());

        Gathering gathering2 = gatheringRepository.save(Gathering.builder()
            .hostUser(user) // 자신이 주최한 모임
            .name("Hosted Gathering")
            .location(Location.HONGDAE)
            .type(Type.MINDFULNESS)
            .dateTime(ZonedDateTime.now().plusDays(2))
            .registrationEnd(ZonedDateTime.now().plusDays(1))
            .capacity(5)
            .build());

        // 자신이 참여한 모임 등록
        userGatheringRepository.save(UserGathering.builder()
            .user(user)
            .gathering(gathering1)
            .build());

        // 자신이 주최한 모임도 추가 (조회에서 제외되어야 함)
        userGatheringRepository.save(UserGathering.builder()
            .user(user)
            .gathering(gathering2)
            .build());

        // When
        Pageable pageable = PageRequest.of(0, 10);
        PagedResponse<MyGatheringResponse> response = gatheringService.getMyGatherings(user.getEmail(), pageable);

        // Then
        assertThat(response.getContent()).hasSize(1); // 주최한 모임은 제외되므로 1개만 반환
        assertThat(response.getContent()).extracting("name").containsExactly("Other Gathering");
    }

    @Test
    @DisplayName("참여한 모임 중 리뷰가 작성된 경우 조회")
    void getMyGatherings_includeReviewStatus() {
        // Given
        User user1 = createUser("testuser1@test.com", "user1");
        User user2 = createUser("testuser2@test.com", "user2");

        Gathering gathering = gatheringRepository.save(Gathering.builder()
            .hostUser(user2)
            .name("Reviewed Gathering")
            .location(Location.SINRIM)
            .type(Type.WORKATION)
            .dateTime(ZonedDateTime.now().plusDays(2))
            .registrationEnd(ZonedDateTime.now().plusDays(1))
            .capacity(5)
            .build());

        userGatheringRepository.save(UserGathering.builder()
            .user(user1)
            .gathering(gathering)
            .build());

        userGatheringRepository.save(UserGathering.builder()
            .user(user2)
            .gathering(gathering)
            .build());

        reviewRepository.save(Review.builder()
            .user(user1)
            .gathering(gathering)
            .comment("Great gathering!")
            .score(5)
            .build());

        // When
        Pageable pageable = PageRequest.of(0, 10);
        PagedResponse<MyGatheringResponse> response = gatheringService.getMyGatherings(user1.getEmail(), pageable);

        // Then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).isReviewed()).isTrue();
    }

    @Test
    @DisplayName("참여한 모임 중 리뷰가 작성되지 않은 경우 조회")
    void getMyGatherings_noReviewStatus() {
        // Given
        User user1 = createUser("testuser1@test.com", "user1");
        User user2 = createUser("testuser2@test.com", "user2");

        Gathering gathering = gatheringRepository.save(Gathering.builder()
            .hostUser(user2)
            .name("Non-Reviewed Gathering")
            .location(Location.EULJIRO3GA)
            .type(Type.OFFICE_STRETCHING)
            .dateTime(ZonedDateTime.now().plusDays(2))
            .registrationEnd(ZonedDateTime.now().plusDays(1))
            .capacity(5)
            .build());

        userGatheringRepository.save(UserGathering.builder()
            .user(user2)
            .gathering(gathering)
            .build());

        userGatheringRepository.save(UserGathering.builder()
            .user(user1)
            .gathering(gathering)
            .build());

        // When
        Pageable pageable = PageRequest.of(0, 10);
        PagedResponse<MyGatheringResponse> response = gatheringService.getMyGatherings(user1.getEmail(), pageable);

        // Then
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).isReviewed()).isFalse();
    }

    private User createUser(String email, String name) {
        User user = User.builder()
            .email(email)
            .name(name)
            .password("password")
            .build();
        return userRepository.save(user);
    }

    private Gathering createGathering(User host, String name) {
        Gathering gathering = Gathering.builder()
            .hostUser(host)
            .name(name)
            .location(Location.KONDAE)
            .dateTime(ZonedDateTime.now().plusDays(1))
            .registrationEnd(ZonedDateTime.now().minusDays(1))
            .capacity(10)
            .build();
        return gatheringRepository.save(gathering);
    }

    private void addUserToGathering(User user, Gathering gathering) {
        UserGathering userGathering = UserGathering.builder()
            .user(user)
            .gathering(gathering)
            .build();
        userGatheringRepository.save(userGathering);
    }
}