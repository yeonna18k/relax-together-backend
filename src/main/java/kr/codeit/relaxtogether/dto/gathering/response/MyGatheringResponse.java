package kr.codeit.relaxtogether.dto.gathering.response;

import java.time.LocalDateTime;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
import kr.codeit.relaxtogether.entity.gathering.Status;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyGatheringResponse {

    private Long id;
    private String type;
    private String name;
    private LocalDateTime dateTime;
    private LocalDateTime registrationEnd;
    private String location;
    private Long participantCount;
    private int capacity;
    private String imageUrl;
    private Long hostUser;
    private Status status;
    private boolean isCompleted;
    private boolean isReviewed;

    public static MyGatheringResponse from(Gathering gathering, long participantCount, boolean isCompleted,
        boolean isReviewed) {
        return MyGatheringResponse.builder()
            .id(gathering.getId())
            .type(gathering.getType().getText())
            .name(gathering.getName())
            .dateTime(gathering.getDateTime())
            .registrationEnd(gathering.getRegistrationEnd())
            .location(gathering.getLocation().getText())
            .participantCount(participantCount)
            .capacity(gathering.getCapacity())
            .imageUrl(gathering.getImageUrl())
            .hostUser(gathering.getHostUser().getId())
            .status(gathering.getStatus())
            .isCompleted(isCompleted)
            .isReviewed(isReviewed)
            .build();
    }
}
