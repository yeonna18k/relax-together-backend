package kr.codeit.relaxtogether.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.codeit.relaxtogether.auth.CustomUserDetails;
import kr.codeit.relaxtogether.dto.PagedResponse;
import kr.codeit.relaxtogether.dto.gathering.request.CreateGatheringRequest;
import kr.codeit.relaxtogether.dto.gathering.request.GatheringSearchCondition;
import kr.codeit.relaxtogether.dto.gathering.response.GatheringDetailResponse;
import kr.codeit.relaxtogether.dto.gathering.response.SearchGatheringResponse;
import kr.codeit.relaxtogether.service.GatheringService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/gatherings")
@RestController
public class GatheringController {

    private final GatheringService gatheringService;

    @Operation(summary = "모임 생성", description = "새로운 모임을 생성합니다.")
    @PostMapping
    public ResponseEntity<Void> createGathering(
        @Valid @RequestBody CreateGatheringRequest request,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        gatheringService.createGathering(request, user.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "모임 목록 조회", description = "모임의 종류, 위치, 날짜 등 다양한 조건으로 모임 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<PagedResponse<SearchGatheringResponse>> searchGatherings(
        GatheringSearchCondition condition,
        @PageableDefault(sort = "registrationEnd", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(gatheringService.search(condition, pageable));
    }

    @Operation(summary = "모임 상세 조회", description = "모임의 상세 정보를 조회합니다.")
    @GetMapping("/{gatheringId}")
    public ResponseEntity<GatheringDetailResponse> getGatheringDetail(@PathVariable Long gatheringId) {
        return ResponseEntity.ok(gatheringService.getGatheringDetail(gatheringId));
    }

    @Operation(summary = "모임 참여", description = "로그인한 사용자가 모임에 참여합니다.")
    @PostMapping("/{gatheringId}/join")
    public ResponseEntity<String> joinGathering(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        gatheringService.joinGathering(gatheringId, user.getUsername());
        return ResponseEntity.ok("모임에 참여했습니다.");
    }

    @Operation(summary = "모임 취소", description = "모임을 취소합니다. 모임 생성자만 취소할 수 있습니다.")
    @PutMapping("/{gatheringId}/cancel")
    public ResponseEntity<String> cancelGathering(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        gatheringService.cancelGathering(gatheringId, user.getUsername());
        return ResponseEntity.ok("모임을 취소했습니다.");
    }
}
