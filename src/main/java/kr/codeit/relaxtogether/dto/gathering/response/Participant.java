package kr.codeit.relaxtogether.dto.gathering.response;

import java.time.ZonedDateTime;
import kr.codeit.relaxtogether.entity.gathering.UserGathering;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Participant {

    private Long userId;
    private String name;
    private String email;
    private String companyName;
    private String profileImage;
    private ZonedDateTime joinedAt;

    public static Participant from(UserGathering userGathering) {
        return Participant.builder()
            .userId(userGathering.getId())
            .name(userGathering.getUser().getName())
            .email(userGathering.getUser().getEmail())
            .companyName(userGathering.getUser().getCompanyName())
            .profileImage(userGathering.getUser().getProfileImage())
            .joinedAt(userGathering.getCreatedDate())
            .build();
    }
}
