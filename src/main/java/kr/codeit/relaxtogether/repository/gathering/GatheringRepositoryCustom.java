package kr.codeit.relaxtogether.repository.gathering;

import kr.codeit.relaxtogether.dto.gathering.request.GatheringSearchCondition;
import kr.codeit.relaxtogether.dto.gathering.response.HostedGatheringResponse;
import kr.codeit.relaxtogether.dto.gathering.response.SearchGatheringResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface GatheringRepositoryCustom {

    Slice<SearchGatheringResponse> searchGatherings(GatheringSearchCondition condition, Pageable pageable);

    Slice<HostedGatheringResponse> findGatheringsWithParticipantCountByHostUserId(Long hostUserId, Pageable pageable);
}
