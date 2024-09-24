package kr.codeit.relaxtogether.controller;

import java.util.List;
import kr.codeit.relaxtogether.auth.CustomUserDetails;
import kr.codeit.relaxtogether.dto.review.request.WriteReviewRequest;
import kr.codeit.relaxtogether.dto.review.response.ReviewDetailsResponse;
import kr.codeit.relaxtogether.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/me")
    public ResponseEntity<List<ReviewDetailsResponse>> loginUserReviews(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ReviewDetailsResponse> loginUserReviews = reviewService.getLoginUserReviews(userDetails.getUsername());
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(loginUserReviews);
    }
}
