package kr.codeit.relaxtogether.repository.review;

import java.util.List;
import kr.codeit.relaxtogether.dto.review.response.ReviewDetailsResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ReviewRepositoryCustom {

    Slice<ReviewDetailsResponse> findReviewsByUserId(Long userId, Pageable pageable);

    List<ReviewDetailsResponse> findReviewsByGatheringId(Long gatheringId, Pageable pageable);
}
