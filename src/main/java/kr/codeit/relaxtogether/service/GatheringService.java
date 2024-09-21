package kr.codeit.relaxtogether.service;

import kr.codeit.relaxtogether.dto.PagedResponse;
import kr.codeit.relaxtogether.dto.gathering.request.CreateGatheringRequest;
import kr.codeit.relaxtogether.dto.gathering.request.GatheringSearchCondition;
import kr.codeit.relaxtogether.dto.gathering.response.GatheringDetailResponse;
import kr.codeit.relaxtogether.dto.gathering.response.SearchGatheringResponse;
import kr.codeit.relaxtogether.entity.User;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
import kr.codeit.relaxtogether.entity.gathering.UserGathering;
import kr.codeit.relaxtogether.repository.UserGatheringRepository;
import kr.codeit.relaxtogether.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final GatheringRepository gatheringRepository;
    private final UserGatheringRepository userGatheringRepository;

    @Transactional
    public void createGathering(CreateGatheringRequest request, String email) {
        validateDateTime(request);
        User user = getUserByEmail(email);

        Gathering gathering = request.toEntity(user);
        gatheringRepository.save(gathering);

        saveUserGathering(user, gathering);
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

    public GatheringDetailResponse getGatheringDetail(Long gatheringId) {
        Gathering gathering = getGatheringBy(gatheringId);

        long participantCount = userGatheringRepository.countByGatheringId(gatheringId);

        return GatheringDetailResponse.from(gathering, participantCount);
    }

    @Transactional
    public void joinGathering(Long gatheringId, String username) {
        User user = getUserByEmail(username);
        Gathering gathering = getGatheringBy(gatheringId);

        if (userGatheringRepository.countByGatheringId(gatheringId) >= gathering.getCapacity()) {
            throw new IllegalArgumentException("해당 모임은 이미 정원이 찼습니다.");
        }

        if (userGatheringRepository.existsByUserIdAndGatheringId(user.getId(), gathering.getId())) {
            throw new IllegalArgumentException("이미 참여한 모임입니다.");
        }

        saveUserGathering(user, gathering);
    }

    private void saveUserGathering(User user, Gathering gathering) {
        UserGathering userGathering = UserGathering.builder()
            .user(user)
            .gathering(gathering)
            .build();
        userGatheringRepository.save(userGathering);
    }

    private User getUserByEmail(String userId) {
        return userRepository.findByEmail(userId)
            .orElseThrow(() -> new IllegalArgumentException("유저정보를 찾을 수 없습니다."));
    }

    private Gathering getGatheringBy(Long gatheringId) {
        return gatheringRepository.findById(gatheringId)
            .orElseThrow(() -> new IllegalArgumentException("해당 모임을 찾을 수 없습니다."));
    }

    private void validateDateTime(CreateGatheringRequest request) {
        if (request.getRegistrationEnd().isAfter(request.getDateTime())) {
            throw new IllegalArgumentException("모집 종료일은 모임 시작일 이전이어야 합니다.");
        }
    }
}
