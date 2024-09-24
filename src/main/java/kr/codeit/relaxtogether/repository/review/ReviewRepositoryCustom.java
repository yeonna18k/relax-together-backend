package kr.codeit.relaxtogether.repository.review;

import java.util.List;
import kr.codeit.relaxtogether.dto.review.response.ReviewDetailsResponse;

public interface ReviewRepositoryCustom {

    List<ReviewDetailsResponse> findReviewsByUserId(Long userId);
}
