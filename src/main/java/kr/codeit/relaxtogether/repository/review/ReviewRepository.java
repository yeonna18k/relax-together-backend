package kr.codeit.relaxtogether.repository.review;

import kr.codeit.relaxtogether.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    boolean existsByUserIdAndGatheringId(Long userId, Long gatheringId);
}
