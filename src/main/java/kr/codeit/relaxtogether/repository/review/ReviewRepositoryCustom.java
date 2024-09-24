package kr.codeit.relaxtogether.repository.review;

import java.util.List;
import kr.codeit.relaxtogether.dto.review.response.ReviewDetailsResponse;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    List<ReviewDetailsResponse> findReviewsByUserId(Long userId);

    List<ReviewDetailsResponse> findReviewsByGatheringId(Long gatheringId, Pageable pageable);
}
