package kr.codeit.relaxtogether.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import kr.codeit.relaxtogether.auth.CustomUserDetails;
import kr.codeit.relaxtogether.dto.PagedResponse;
import kr.codeit.relaxtogether.dto.review.request.ReviewSearchCondition;
import kr.codeit.relaxtogether.dto.review.request.WriteReviewRequest;
import kr.codeit.relaxtogether.dto.review.response.GatheringReviewsResponse;
import kr.codeit.relaxtogether.dto.review.response.ReviewDetailsResponse;
import kr.codeit.relaxtogether.dto.review.response.ReviewScoreCountResponse;
import kr.codeit.relaxtogether.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "리뷰를 작성합니다.")
    @PostMapping("/reviews")
    public ResponseEntity<String> writeReview(@RequestBody WriteReviewRequest writeReviewRequest,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.writeReview(writeReviewRequest, userDetails.getUsername());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("success");
    }

    @Operation(summary = "내가 작성한 리뷰 목록 조회", description = "내가 작성한 리뷰 목록을 조회합니다.")
    @GetMapping("/reviews/me")
    public ResponseEntity<PagedResponse<ReviewDetailsResponse>> loginUserReviews(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Parameter(description = "조회 시작 위치 (최소 0)")
        @RequestParam(value = "page", defaultValue = "0") int page,
        @Parameter(description = "한 번에 조회할 리뷰 수 (최소 1)")
        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<ReviewDetailsResponse> loginUserReviews = reviewService.getLoginUserReviews(
            userDetails.getUsername(), pageable);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(loginUserReviews);
    }

    @Operation(summary = "모임 리뷰 목록 조회", description = "해당 모임에 작성된 리뷰 목록을 조회합니다.")
    @GetMapping("/gatherings/{gatheringId}/reviews")
    public ResponseEntity<GatheringReviewsResponse> getReviewsByGatheringId(
        @PathVariable Long gatheringId,
        @Parameter(description = "조회 시작 위치 (최소 0)")
        @RequestParam(value = "page", defaultValue = "0") int page,
        @Parameter(description = "한 번에 조회할 리뷰 수 (최소 1)")
        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        GatheringReviewsResponse reviewsByGatheringId = reviewService.getReviewsByGatheringId(gatheringId, pageable);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(reviewsByGatheringId);
    }

    @Operation(summary = "리뷰 목록 조회", description = "필터링과 정렬 조건에 따른 리뷰 목록을 조회합니다.")
    @GetMapping("/reviews")
    public ResponseEntity<PagedResponse<ReviewDetailsResponse>> getReviewsByConditions(
        ReviewSearchCondition reviewSearchCondition,
        @Parameter(description = "정렬조건을 선택하세요 [createdDate, score, participantCount]")
        @RequestParam(value = "sortBy", defaultValue = "createdDate") String sortBy,
        @Parameter(description = "조회 시작 위치 (최소 0)")
        @RequestParam(value = "page", defaultValue = "0") int page,
        @Parameter(description = "한 번에 조회할 모임 수 (최소 1)")
        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<ReviewDetailsResponse> reviewsByConditions = reviewService.getReviewsByConditions(
            reviewSearchCondition, pageable);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(reviewsByConditions);
    }

    @Operation(summary = "평점별 리뷰 개수 확인", description = "조건에 따라 리뷰를 필터링하고 해당 리뷰에서 평점별로 개수를 확인합니다.")
    @GetMapping("/reviews/scores")
    public ResponseEntity<ReviewScoreCountResponse> getReviewScoreCounts(
        @Parameter(description = "모임 타입을 선택하세요 [달램핏, 워케이션]", example = "워케이션")
        @RequestParam String type,
        @Parameter(description = "모임 타입의 상세 내용을 선택하세요 [마인드풀니스, 오피스 스트레칭]", example = "null")
        @RequestParam String typeDetail
    ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(reviewService.getReviewScoreCounts(type, typeDetail));
    }
}
