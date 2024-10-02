package kr.codeit.relaxtogether.dto.gathering.response;

import java.util.List;
import kr.codeit.relaxtogether.entity.gathering.UserGathering;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class ParticipantsResponse {

    private Long gatheringId;
    private List<Participant> participants;
    private int currentPage;
    private int totalPages;
    private long totalElements;

    public static ParticipantsResponse from(Page<UserGathering> userGatherings, Long gatheringId) {
        List<Participant> participants = userGatherings.getContent().stream()
            .map(Participant::from)
            .toList();

        return ParticipantsResponse.builder()
            .gatheringId(gatheringId)
            .participants(participants)
            .currentPage(userGatherings.getNumber())
            .totalPages(userGatherings.getTotalPages())
            .totalElements(userGatherings.getTotalElements())
            .build();
    }
}
