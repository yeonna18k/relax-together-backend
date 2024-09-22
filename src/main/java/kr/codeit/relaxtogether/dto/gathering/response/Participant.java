package kr.codeit.relaxtogether.dto.gathering.response;

import java.time.LocalDateTime;
import kr.codeit.relaxtogether.entity.gathering.UserGathering;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Participant {

    Long userId;
    String name;
    String email;
    String companyName;
    String profileImage;
    LocalDateTime joinedAt;

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
