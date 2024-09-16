package kr.codeit.relaxtogether.service;

import kr.codeit.relaxtogether.dto.gathering.CreateGatheringRequest;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
import kr.codeit.relaxtogether.entity.gathering.UserGathering;
import kr.codeit.relaxtogether.repository.GatheringRepository;
import kr.codeit.relaxtogether.repository.UserGatheringRepository;
import lombok.RequiredArgsConstructor;
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
        Gathering gathering = request.toEntity();
        gatheringRepository.save(gathering);

        UserGathering userGathering = UserGathering.builder()
            .gathering(gathering)
            .build();
        userGatheringRepository.save(userGathering);
    }
}
