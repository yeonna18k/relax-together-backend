package kr.codeit.relaxtogether.controller;

import static org.springframework.data.domain.Sort.Direction.ASC;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.codeit.relaxtogether.auth.CustomUserDetails;
import kr.codeit.relaxtogether.dto.PagedResponse;
import kr.codeit.relaxtogether.dto.gathering.request.CreateGatheringRequest;
import kr.codeit.relaxtogether.dto.gathering.request.GatheringSearchCondition;
import kr.codeit.relaxtogether.dto.gathering.response.GatheringDetailResponse;
import kr.codeit.relaxtogether.dto.gathering.response.HostedGatheringResponse;
import kr.codeit.relaxtogether.dto.gathering.response.ParticipantsResponse;
import kr.codeit.relaxtogether.dto.gathering.response.SearchGatheringResponse;
import kr.codeit.relaxtogether.service.GatheringService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
        @PageableDefault(sort = "registrationEnd", direction = ASC) Pageable pageable
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

    @Operation(summary = "모임 참여 취소", description = "사용자가 모임에서 참여 취소합니다. 이미 지난 모임은 참여 취소가 불가합니다.")
    @DeleteMapping("/{gatheringId}/leave")
    public ResponseEntity<String> leaveGathering(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        gatheringService.leaveGathering(gatheringId, user.getUsername());
        return ResponseEntity.ok("모임 참여를 취소 합니다.");
    }

    @Operation(summary = "특정 모임의 참가자 목록 조회", description = "특정 모임의 참가자 목록을 페이지네이션하여 조회합니다.(기본 정렬 참여 날짜 ASC)")
    @GetMapping("/{gatheringId}/participants")
    public ResponseEntity<ParticipantsResponse> getParticipants(
        @PathVariable Long gatheringId,
        @PageableDefault(size = 5, sort = "createdDate", direction = ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(gatheringService.getParticipants(gatheringId, pageable));
    }

    @Operation(summary = "내가 만든 모임 목록 조회", description = "내가 만든 모임 목록을 조회합니다.(정렬 모임 생성 날짜 DESC)")
    @GetMapping("/my-hosted")
    public ResponseEntity<PagedResponse<HostedGatheringResponse>> getMyHostedGatherings(
        @AuthenticationPrincipal CustomUserDetails user,
        @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(gatheringService.getMyHostedGatherings(user.getUsername(), pageable));
    }
}
