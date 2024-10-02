package kr.codeit.relaxtogether.dto.gathering.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.ZonedDateTime;
import kr.codeit.relaxtogether.entity.gathering.Location;
import kr.codeit.relaxtogether.entity.gathering.Type;
import lombok.Getter;

@Getter
public class HostedGatheringResponse {

    private Long id;
    private String type;
    private String name;
    private ZonedDateTime dateTime;
    private ZonedDateTime registrationEnd;
    private String location;
    private Long participantCount;
    private int capacity;
    private String imageUrl;
    private Long hostUser;

    @QueryProjection
    public HostedGatheringResponse(Long id, Type type, String name, ZonedDateTime dateTime,
        ZonedDateTime registrationEnd,
        Location location, Long participantCount, int capacity, String imageUrl, Long hostUser) {
        this.id = id;
        this.type = type.getText();
        this.name = name;
        this.dateTime = dateTime;
        this.registrationEnd = registrationEnd;
        this.location = location.getText();
        this.participantCount = participantCount;
        this.capacity = capacity;
        this.imageUrl = imageUrl;
        this.hostUser = hostUser;
    }
}
