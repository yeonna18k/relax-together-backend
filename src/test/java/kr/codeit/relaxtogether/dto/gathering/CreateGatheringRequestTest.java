package kr.codeit.relaxtogether.dto.gathering;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;
import kr.codeit.relaxtogether.dto.gathering.request.CreateGatheringRequest;
import kr.codeit.relaxtogether.entity.gathering.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateGatheringRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("타입이 워케이션일 경우 모임 이름이 공백일 때 예외가 발생한다.")
    @Test
    void whenTypeIsWorkation_thenNameMustBeNotBlank() {
        // Given
        CreateGatheringRequest request = CreateGatheringRequest.builder()
            .name("")
            .location("건대입구")
            .imageUrl("some-image-url")
            .type(Type.WORKATION.getText())
            .dateTime(LocalDateTime.now().plusDays(1))
            .registrationEnd(LocalDateTime.now().plusDays(2))
            .capacity(10)
            .build();

        // When
        Set<ConstraintViolation<CreateGatheringRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).anyMatch(v ->
            v.getMessage().equals("모임 이름은 워케이션일 경우 필수입니다."));
    }

    @DisplayName("타입이 워케이션일 경우 모임 이름이 null일 때 예외가 발생한다.")
    @Test
    void whenTypeIsWorkation_thenNameMustBeNotNull() {
        // Given
        CreateGatheringRequest request = CreateGatheringRequest.builder()
            .name(null)
            .location("건대입구")
            .imageUrl("some-image-url")
            .type(Type.WORKATION.getText())
            .dateTime(LocalDateTime.now().plusDays(1))
            .registrationEnd(LocalDateTime.now().plusDays(2))
            .capacity(10)
            .build();

        // When
        Set<ConstraintViolation<CreateGatheringRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).anyMatch(v ->
            v.getMessage().equals("모임 이름은 워케이션일 경우 필수입니다."));
    }

    @DisplayName("타입이 워케이션일 경우 모임이름은 필수이다.")
    @Test
    void whenTypeIsWorkation_andNameIsPresent_thenValidationPasses() {
        // Given
        CreateGatheringRequest request = CreateGatheringRequest.builder()
            .name("워크숍")
            .location("홍대입구")
            .imageUrl("some-image-url")
            .type(Type.WORKATION.getText())
            .dateTime(LocalDateTime.now().plusDays(1))
            .registrationEnd(LocalDateTime.now().plusDays(2))
            .capacity(10)
            .build();

        // When
        Set<ConstraintViolation<CreateGatheringRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @DisplayName("타입이 워케이션이 아닐 때 모임이름에 공백이 올 수 있다.")
    @Test
    void whenTypeIsNotWorkation_thenNameCanBeBlank() {
        // Given
        CreateGatheringRequest request = CreateGatheringRequest.builder()
            .name("")
            .location("을지로3가")
            .imageUrl("some-image-url")
            .type(Type.MINDFULNESS.getText())
            .dateTime(LocalDateTime.now().plusDays(1))
            .registrationEnd(LocalDateTime.now().plusDays(2))
            .capacity(10)
            .build();

        // When
        Set<ConstraintViolation<CreateGatheringRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @DisplayName("타입이 워케이션이 아닐 경우 모임 이름은 선택이다.")
    @Test
    void whenTypeIsMindfulness_andNameIsPresent_thenValidationPasses() {
        // Given
        CreateGatheringRequest request = CreateGatheringRequest.builder()
            .name("명상 클래스")
            .location("신림")
            .imageUrl("some-image-url")
            .type(Type.MINDFULNESS.getText())
            .dateTime(LocalDateTime.now().plusDays(1))
            .registrationEnd(LocalDateTime.now().plusDays(1))
            .capacity(10)
            .build();

        // When
        Set<ConstraintViolation<CreateGatheringRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @DisplayName("타입이 워케이션이 아닐 때 모임이름에 null이 올 수 있다.")
    @Test
    void whenTypeIsNotWorkation_thenNameCanBeNull() {
        // Given
        CreateGatheringRequest request = CreateGatheringRequest.builder()
            .name(null)
            .location("을지로3가")
            .imageUrl("some-image-url")
            .type(Type.MINDFULNESS.getText())
            .dateTime(LocalDateTime.now().plusDays(1))
            .registrationEnd(LocalDateTime.now().plusDays(2))
            .capacity(10)
            .build();

        // When
        Set<ConstraintViolation<CreateGatheringRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }
}