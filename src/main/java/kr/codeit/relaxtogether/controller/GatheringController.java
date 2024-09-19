package kr.codeit.relaxtogether.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.codeit.relaxtogether.dto.PagedResponse;
import kr.codeit.relaxtogether.dto.gathering.request.CreateGatheringRequest;
import kr.codeit.relaxtogether.dto.gathering.request.GatheringSearchCondition;
import kr.codeit.relaxtogether.dto.gathering.response.SearchGatheringResponse;
import kr.codeit.relaxtogether.service.GatheringService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/gatherings")
@RestController
public class GatheringController {

    private final GatheringService gatheringService;

    @Operation(summary = "모임 생성", description = "모임 생성 API")
    @PostMapping
    public ResponseEntity<Void> createGathering(@Valid @RequestBody CreateGatheringRequest request) {
        gatheringService.createGathering(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "모임 목록 조회", description = "모임의 종류, 위치, 날짜 등 다양한 조건으로 모임 목록을 조회합니다")
    @GetMapping
    public ResponseEntity<PagedResponse<SearchGatheringResponse>> searchGatherings(
        GatheringSearchCondition condition,
        @PageableDefault(sort = "registrationEnd", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(gatheringService.search(condition, pageable));
    }
}
