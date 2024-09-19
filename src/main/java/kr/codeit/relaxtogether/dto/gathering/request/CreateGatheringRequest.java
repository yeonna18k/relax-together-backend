package kr.codeit.relaxtogether.dto.gathering.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
import kr.codeit.relaxtogether.entity.gathering.Location;
import kr.codeit.relaxtogether.entity.gathering.Type;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateGatheringRequest {

    @Schema(description = "모임 이름", example = "건강한 몸 만들기")
    private String name;

    @Schema(description = "모임 장소", allowableValues = {"건대입구", "홍대입구", "을지로3가", "신림"})
    @NotBlank(message = "장소는 필수입니다.")
    private String location;

    @Schema(description = "모임 이미지", type = "string", example = "https://firebasestorage.googleapis.com/b0/b/example")
    private String imageUrl;

    @Schema(description = "모임 서비스 종류", allowableValues = {"워케이션", "마인드풀니스", "오피스 스트레칭"})
    @NotBlank(message = "서비스 타입은 필수입니다.")
    private String type;

    @Schema(description = "모임 날짜 및 시간 (YYYY-MM-DDTHH:MM:SS)")
    @Future(message = "모임 시작일은 현재 시간 이후여야 합니다.")
    private LocalDateTime dateTime;

    @Schema(description = "모집 마감 날짜 및 시간 (선택 사항, YYYY-MM-DDTHH:MM:SS)")
    @Future(message = "모집 종료일은 현재 시간 이후여야 합니다.")
    private LocalDateTime registrationEnd;

    @Schema(description = "모집 정원 (최소 5명 이상)", example = "10")
    @Min(value = 5, message = "모집 정원은 최소 5명 이상이어야 합니다.")
    private int capacity;

    @Builder
    private CreateGatheringRequest(String name, String location, String imageUrl, String type, LocalDateTime dateTime,
        LocalDateTime registrationEnd, int capacity) {
        this.name = name;
        this.location = location;
        this.imageUrl = imageUrl;
        this.type = type;
        this.dateTime = dateTime;
        this.registrationEnd = registrationEnd;
        this.capacity = capacity;
    }

    @AssertTrue(message = "모임 이름은 워케이션일 경우 필수입니다.")
    private boolean isValidName() {
        Type enumType = Type.fromText(type);
        return enumType != Type.WORKATION || (name != null && !name.isBlank());
    }

    public Gathering toEntity() {
        return Gathering.builder()
            .name(name)
            .location(Location.fromText(location))
            .imageUrl(imageUrl)
            .type(Type.fromText(type))
            .dateTime(dateTime)
            .registrationEnd(registrationEnd)
            .capacity(capacity)
            .build();
    }
}
