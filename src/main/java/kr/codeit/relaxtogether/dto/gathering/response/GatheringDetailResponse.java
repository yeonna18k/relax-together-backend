package kr.codeit.relaxtogether.dto.gathering.response;

import java.time.LocalDateTime;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GatheringDetailResponse {

    private Long id;
    private Long createdBy;
    private String name;
    private String location;
    private String imageUrl;
    private String type;
    private LocalDateTime dateTime;
    private LocalDateTime registrationEnd;
    private int capacity;

    @Builder
    private GatheringDetailResponse(Long id, Long createdBy, String name, String location, String imageUrl, String type,
        LocalDateTime dateTime, LocalDateTime registrationEnd, int capacity) {
        this.id = id;
        this.createdBy = createdBy;
        this.name = name;
        this.location = location;
        this.imageUrl = imageUrl;
        this.type = type;
        this.dateTime = dateTime;
        this.registrationEnd = registrationEnd;
        this.capacity = capacity;
    }

    public static GatheringDetailResponse of(Gathering gathering) {
        return GatheringDetailResponse.builder()
            .id(gathering.getId())
            .createdBy(gathering.getCreatedBy().getId())
            .name(gathering.getName())
            .location(gathering.getLocation().getText())
            .imageUrl(gathering.getImageUrl())
            .type(gathering.getType().getText())
            .dateTime(gathering.getDateTime())
            .registrationEnd(gathering.getRegistrationEnd())
            .capacity(gathering.getCapacity())
            .build();
    }
}
