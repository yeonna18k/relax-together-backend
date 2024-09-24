package kr.codeit.relaxtogether.repository.review;

import static kr.codeit.relaxtogether.entity.QReview.review;
import static kr.codeit.relaxtogether.entity.QUser.user;
import static kr.codeit.relaxtogether.entity.gathering.QGathering.gathering;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.codeit.relaxtogether.dto.review.response.ReviewDetailsResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReviewDetailsResponse> findReviewsByUserId(Long userId) {
        return queryFactory.select(
                Projections.constructor(
                    ReviewDetailsResponse.class,
                    gathering.type,
                    gathering.location,
                    user.profileImage,
                    user.name,
                    review.score,
                    review.comment,
                    review.createdDate
                )
            )
            .from(review)
            .join(review.user)
            .join(review.gathering)
            .where(review.user.id.eq(userId))
            .fetch();
    }
}
