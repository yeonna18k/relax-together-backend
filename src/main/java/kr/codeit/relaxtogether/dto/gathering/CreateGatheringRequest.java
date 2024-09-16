package kr.codeit.relaxtogether.dto.gathering;

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

    @NotBlank(message = "모임 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "장소는 필수입니다.")
    private String location;

    private String image;

    @NotBlank(message = "서비스 타입은 필수입니다.")
    private String type;

    @Future(message = "날짜는 현재 시간 이후여야 합니다.")
    private LocalDateTime dateTime;

    @Future(message = "모집 종료일은 현재 시간 이후여야 합니다.")
    private LocalDateTime registrationEnd;

    @Min(value = 5, message = "모집 정원은 최소 5명 이상이어야 합니다.")
    private int capacity;

    @Builder
    private CreateGatheringRequest(String name, String location, String image, String type, LocalDateTime dateTime,
        LocalDateTime registrationEnd, int capacity) {
        this.name = name;
        this.location = location;
        this.image = image;
        this.type = type;
        this.dateTime = dateTime;
        this.registrationEnd = registrationEnd;
        this.capacity = capacity;
    }

    public Gathering toEntity() {
        return Gathering.builder()
            .name(name)
            .location(Location.fromText(location))
            .image(image)
            .type(Type.fromText(type))
            .dateTime(dateTime)
            .registrationEnd(registrationEnd)
            .capacity(capacity)
            .build();
    }
}
