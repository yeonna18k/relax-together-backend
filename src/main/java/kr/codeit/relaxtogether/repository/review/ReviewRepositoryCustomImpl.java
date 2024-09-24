package kr.codeit.relaxtogether.repository.review;

import static kr.codeit.relaxtogether.entity.QReview.review;
import static kr.codeit.relaxtogether.entity.QUser.user;
import static kr.codeit.relaxtogether.entity.gathering.QGathering.gathering;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import kr.codeit.relaxtogether.dto.review.response.ReviewDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<ReviewDetailsResponse> findReviewsByUserId(Long userId, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        List<ReviewDetailsResponse> results = queryFactory.select(
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
            .orderBy(review.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageSize + 1)
            .fetch();

        boolean hasNext = false;
        if (results.size() > pageSize) {
            results.remove(pageSize);
            hasNext = true;
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public List<ReviewDetailsResponse> findReviewsByGatheringId(Long gatheringId, Pageable pageable) {
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
            .where(review.gathering.id.eq(gatheringId))
            .orderBy(review.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }
}
