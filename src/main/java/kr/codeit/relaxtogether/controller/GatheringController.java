package kr.codeit.relaxtogether.controller;

import jakarta.validation.Valid;
import kr.codeit.relaxtogether.dto.gathering.CreateGatheringRequest;
import kr.codeit.relaxtogether.service.GatheringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/gatherings")
@RestController
public class GatheringController {

    private final GatheringService gatheringService;

    public ResponseEntity<Void> createGathering(@Valid @RequestBody CreateGatheringRequest request) {
        gatheringService.createGathering(request);
        return ResponseEntity.ok().build();
    }
}
