package kr.codeit.relaxtogether.service;

import kr.codeit.relaxtogether.dto.PagedResponse;
import kr.codeit.relaxtogether.dto.gathering.request.CreateGatheringRequest;
import kr.codeit.relaxtogether.dto.gathering.request.GatheringSearchCondition;
import kr.codeit.relaxtogether.dto.gathering.response.SearchGatheringResponse;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
import kr.codeit.relaxtogether.entity.gathering.UserGathering;
import kr.codeit.relaxtogether.repository.UserGatheringRepository;
import kr.codeit.relaxtogether.repository.gathering.GatheringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GatheringService {

    private final GatheringRepository gatheringRepository;
    private final UserGatheringRepository userGatheringRepository;

    @Transactional
    public void createGathering(CreateGatheringRequest request) {

        if (request.getRegistrationEnd().isAfter(request.getDateTime())) {
            throw new IllegalArgumentException("모집 종료일은 모임 시작일 이전이어야 합니다.");
        }

        Gathering gathering = request.toEntity();
        gatheringRepository.save(gathering);

        UserGathering userGathering = UserGathering.builder()
            .gathering(gathering)
            .build();
        userGatheringRepository.save(userGathering);
    }

    public PagedResponse<SearchGatheringResponse> search(GatheringSearchCondition condition, Pageable pageable) {
        Slice<SearchGatheringResponse> gatherings = gatheringRepository.searchGatherings(condition,
            pageable);
        return new PagedResponse<>(
            gatherings.getContent(),
            gatherings.hasNext(),
            gatherings.getNumberOfElements()
        );
    }
}
