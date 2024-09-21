package kr.codeit.relaxtogether.dto.gathering.response;

import java.time.LocalDateTime;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GatheringDetailResponse {

    private Long id;
    private Long hostUser;
    private String name;
    private String location;
    private String type;
    private int capacity;
    private String imageUrl;
    private Long participantCount;
    private LocalDateTime dateTime;
    private LocalDateTime registrationEnd;
    private String status;

    @Builder
    private GatheringDetailResponse(Long id, Long hostUser, String name, String location, String type, int capacity,
        String imageUrl, Long participantCount, LocalDateTime dateTime, LocalDateTime registrationEnd, String status) {
        this.id = id;
        this.hostUser = hostUser;
        this.name = name;
        this.location = location;
        this.type = type;
        this.capacity = capacity;
        this.imageUrl = imageUrl;
        this.participantCount = participantCount;
        this.dateTime = dateTime;
        this.registrationEnd = registrationEnd;
        this.status = status;
    }

    public static GatheringDetailResponse from(Gathering gathering, long participantCount) {
        return GatheringDetailResponse.builder()
            .id(gathering.getId())
            .hostUser(gathering.getHostUser().getId())
            .name(gathering.getName())
            .location(gathering.getLocation().getText())
            .type(gathering.getType().getText())
            .capacity(gathering.getCapacity())
            .participantCount(participantCount)
            .imageUrl(gathering.getImageUrl())
            .dateTime(gathering.getDateTime())
            .registrationEnd(gathering.getRegistrationEnd())
            .status(gathering.getStatus().toString())
            .build();
    }
}
