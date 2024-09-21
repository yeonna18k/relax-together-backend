package kr.codeit.relaxtogether.dto.gathering.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import kr.codeit.relaxtogether.entity.gathering.Location;
import kr.codeit.relaxtogether.entity.gathering.Type;
import lombok.Getter;

@Getter
public class SearchGatheringResponse {

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

    @QueryProjection
    public SearchGatheringResponse(Long id, Type type, String name, LocalDateTime dateTime,
        LocalDateTime registrationEnd, Location location, Long participantCount, int capacity,
        String imageUrl, Long hostUser) {
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
