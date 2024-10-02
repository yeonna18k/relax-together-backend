package kr.codeit.relaxtogether.service;

import static kr.codeit.relaxtogether.exception.ErrorCode.AUTHENTICATION_FAIL;
import static kr.codeit.relaxtogether.exception.ErrorCode.AUTHORIZATION_FAIL;
import static kr.codeit.relaxtogether.exception.ErrorCode.GATHERING_ALREADY_JOINED;
import static kr.codeit.relaxtogether.exception.ErrorCode.GATHERING_CAPACITY_FULL;
import static kr.codeit.relaxtogether.exception.ErrorCode.GATHERING_DATE_VALIDATION_ERROR;
import static kr.codeit.relaxtogether.exception.ErrorCode.GATHERING_HOST_CANNOT_LEAVE;
import static kr.codeit.relaxtogether.exception.ErrorCode.GATHERING_NOT_FOUND;
import static kr.codeit.relaxtogether.exception.ErrorCode.GATHERING_PAST_DATE;
import static kr.codeit.relaxtogether.exception.ErrorCode.PARTICIPATION_NOT_FOUND;

import java.time.ZonedDateTime;
import java.util.List;
import kr.codeit.relaxtogether.dto.PagedResponse;
import kr.codeit.relaxtogether.dto.gathering.request.CreateGatheringRequest;
import kr.codeit.relaxtogether.dto.gathering.request.GatheringSearchCondition;
import kr.codeit.relaxtogether.dto.gathering.response.GatheringDetailResponse;
import kr.codeit.relaxtogether.dto.gathering.response.HostedGatheringResponse;
import kr.codeit.relaxtogether.dto.gathering.response.MyGatheringResponse;
import kr.codeit.relaxtogether.dto.gathering.response.ParticipantsResponse;
import kr.codeit.relaxtogether.dto.gathering.response.SearchGatheringResponse;
import kr.codeit.relaxtogether.entity.User;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
import kr.codeit.relaxtogether.entity.gathering.Status;
import kr.codeit.relaxtogether.entity.gathering.UserGathering;
import kr.codeit.relaxtogether.exception.ApiException;
import kr.codeit.relaxtogether.repository.UserGatheringRepository;
import kr.codeit.relaxtogether.repository.UserRepository;
import kr.codeit.relaxtogether.repository.gathering.GatheringRepository;
import kr.codeit.relaxtogether.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final ReviewRepository reviewRepository;

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
            throw new ApiException(GATHERING_CAPACITY_FULL);
        }

        if (userGatheringRepository.existsByUserIdAndGatheringId(user.getId(), gathering.getId())) {
            throw new ApiException(GATHERING_ALREADY_JOINED);
        }

        saveUserGathering(user, gathering);
    }

    @Transactional
    public void cancelGathering(Long gatheringId, String userId) {
        User user = getUserByEmail(userId);

        Gathering gathering = gatheringRepository.findByIdAndHostUserId(gatheringId, user.getId())
            .orElseThrow(() -> new ApiException(AUTHORIZATION_FAIL));
        gathering.cancel();

        userGatheringRepository.deleteByUserIdAndGatheringId(user.getId(), gatheringId);
    }

    @Transactional
    public void leaveGathering(Long gatheringId, String userId) {
        User user = getUserByEmail(userId);
        Gathering gathering = getGatheringBy(gatheringId);

        if (gathering.isHost(user)) {
            throw new ApiException(GATHERING_HOST_CANNOT_LEAVE);
        }

        if (gathering.hasEnded()) {
            throw new ApiException(GATHERING_PAST_DATE);
        }

        if (!userGatheringRepository.existsByUserIdAndGatheringId(user.getId(), gatheringId)) {
            throw new ApiException(PARTICIPATION_NOT_FOUND);
        }

        userGatheringRepository.deleteByUserIdAndGatheringId(user.getId(), gatheringId);
    }

    public ParticipantsResponse getParticipants(Long gatheringId, Pageable pageable) {
        Gathering gathering = getGatheringBy(gatheringId);
        Page<UserGathering> participantsPage = userGatheringRepository.findWithUserByGatheringId(gathering.getId(),
            pageable);

        return ParticipantsResponse.from(participantsPage, gathering.getId());
    }

    public PagedResponse<HostedGatheringResponse> getMyHostedGatherings(String userId, Pageable pageable) {
        User user = getUserByEmail(userId);
        Slice<HostedGatheringResponse> gatherings = gatheringRepository.findGatheringsWithParticipantCountByHostUserId(
            user.getId(), pageable);

        return new PagedResponse<>(
            gatherings.getContent(),
            gatherings.hasNext(),
            gatherings.getNumberOfElements()
        );
    }

    public PagedResponse<MyGatheringResponse> getMyGatherings(String userId, Pageable pageable) {
        User user = getUserByEmail(userId);

        Slice<UserGathering> gatherings = userGatheringRepository.findNonHostGatheringsByUserIdWithGathering(user.getId(), pageable);
        List<MyGatheringResponse> myGatherings = gatherings.getContent().stream()
            .map(userGathering -> {
                Gathering gathering = userGathering.getGathering();
                Long participantCount = userGatheringRepository.countByGatheringId(gathering.getId());
                boolean isReviewed = reviewRepository.existsByUserIdAndGatheringId(user.getId(), gathering.getId());
                return MyGatheringResponse.from(gathering, participantCount, validateComplete(gathering), isReviewed);
            })
            .toList();

        return new PagedResponse<>(
            myGatherings,
            gatherings.hasNext(),
            gatherings.getNumberOfElements()
        );
    }

    private boolean validateComplete(Gathering gathering) {
        return gathering.getStatus() != Status.CANCELLED
            && gathering.getRegistrationEnd().isBefore(ZonedDateTime.now());
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
            .orElseThrow(() -> new ApiException(AUTHENTICATION_FAIL));
    }

    private Gathering getGatheringBy(Long gatheringId) {
        return gatheringRepository.findById(gatheringId)
            .orElseThrow(() -> new ApiException(GATHERING_NOT_FOUND));
    }

    private void validateDateTime(CreateGatheringRequest request) {
        if (request.getRegistrationEnd().isAfter(request.getDateTime())) {
            throw new ApiException(GATHERING_DATE_VALIDATION_ERROR);
        }
    }
}
