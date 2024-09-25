package kr.codeit.relaxtogether.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import kr.codeit.relaxtogether.auth.CustomUserDetails;
import kr.codeit.relaxtogether.dto.PagedResponse;
import kr.codeit.relaxtogether.dto.review.request.ReviewSearchCondition;
import kr.codeit.relaxtogether.dto.review.request.WriteReviewRequest;
import kr.codeit.relaxtogether.dto.review.response.ReviewDetailsResponse;
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
@RequestMapping("/api/reviews")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<String> writeReview(@RequestBody WriteReviewRequest writeReviewRequest,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.writeReview(writeReviewRequest, userDetails.getUsername());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("success");
    }

    @Operation(summary = "내가 작성한 리뷰 목록 조회", description = "내가 작성한 리뷰 목록을 조회합니다.")
    @GetMapping("/me")
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
    @GetMapping("/{gatheringId}")
    public ResponseEntity<List<ReviewDetailsResponse>> getReviewsByGatheringId(
        @PathVariable Long gatheringId,
        @Parameter(description = "조회 시작 위치 (최소 0)")
        @RequestParam(value = "page", defaultValue = "0") int page,
        @Parameter(description = "한 번에 조회할 리뷰 수 (최소 1)")
        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        List<ReviewDetailsResponse> reviewsByGatheringId = reviewService.getReviewsByGatheringId(gatheringId, pageable);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(reviewsByGatheringId);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ReviewDetailsResponse>> getReviewsByConditions(
        ReviewSearchCondition reviewSearchCondition,
        @Parameter(description = "정렬조건을 선택하세요 [createdDate, score, participantCount]")
        @RequestParam String sortBy,
        @Parameter(description = "조회 시작 위치 (최소 0)")
        @RequestParam int page,
        @Parameter(description = "한 번에 조회할 모임 수 (최소 1)")
        @RequestParam int size
    ) {
        Sort sort = Sort.by(sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<ReviewDetailsResponse> reviewsByConditions = reviewService.getReviewsByConditions(
            reviewSearchCondition, pageable);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(reviewsByConditions);
    }
}
