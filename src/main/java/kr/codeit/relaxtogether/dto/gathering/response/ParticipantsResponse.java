package kr.codeit.relaxtogether.dto.gathering.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipantsResponse {

    Long gatheringId;
    List<Participant> participants;
}
