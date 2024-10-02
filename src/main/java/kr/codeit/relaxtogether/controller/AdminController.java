package kr.codeit.relaxtogether.controller;

import kr.codeit.relaxtogether.repository.UserGatheringRepository;
import kr.codeit.relaxtogether.repository.gathering.GatheringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminController {

    private final UserGatheringRepository userGatheringRepository;
    private final GatheringRepository gatheringRepository;

    @Transactional
    @DeleteMapping("/api/delete_all_gatherings")
    public ResponseEntity<String> deleteAllGatherings() {
        userGatheringRepository.deleteAll();
        gatheringRepository.deleteAll();

        return ResponseEntity.ok("모든 모임정보가 삭제되었습니다.");
    }
}
