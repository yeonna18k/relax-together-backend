package kr.codeit.relaxtogether.repository.review;

import static kr.codeit.relaxtogether.entity.QReview.review;
import static kr.codeit.relaxtogether.entity.QUser.user;
import static kr.codeit.relaxtogether.entity.gathering.QGathering.gathering;
import static kr.codeit.relaxtogether.entity.gathering.QUserGathering.userGathering;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.ZonedDateTime;
import java.util.List;
import kr.codeit.relaxtogether.dto.review.request.ReviewSearchCondition;
import kr.codeit.relaxtogether.dto.review.response.GatheringReviewsResponse;
import kr.codeit.relaxtogether.dto.review.response.ReviewDetailsResponse;
import kr.codeit.relaxtogether.dto.review.response.ReviewScoreCountResponse;
import kr.codeit.relaxtogether.entity.Review;
import kr.codeit.relaxtogether.entity.gathering.Location;
import kr.codeit.relaxtogether.entity.gathering.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

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
            .limit(pageSize + 1L)
            .fetch();

        boolean hasNext = false;
        if (results.size() > pageSize) {
            results.remove(pageSize);
            hasNext = true;
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public GatheringReviewsResponse findReviewsByGatheringId(Long gatheringId, Pageable pageable) {
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
            .where(review.gathering.id.eq(gatheringId))
            .orderBy(review.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long totalCount = queryFactory.select(review.count())
            .from(review)
            .join(review.user)
            .join(review.gathering)
            .where(review.gathering.id.eq(gatheringId))
            .fetchOne();
        if (totalCount == null) {
            totalCount = 0L;
        }
        Page<ReviewDetailsResponse> page = new PageImpl<>(results, pageable, totalCount);

        return GatheringReviewsResponse.of(results, page);
    }

    @Override
    public Slice<ReviewDetailsResponse> findReviewsByConditions(ReviewSearchCondition reviewSearchCondition,
        Pageable pageable) {
        int pageSize = pageable.getPageSize();
        JPAQuery<ReviewDetailsResponse> query = createBaseQuery();
        query.where(
            gatheringTypeEq(reviewSearchCondition.getType(), reviewSearchCondition.getTypeDetail()),
            locationEq(reviewSearchCondition.getLocation()),
            dateEq(reviewSearchCondition.getDate())
        );

        Sort sort = pageable.getSort();
        String sortBy = sort.iterator().next().getProperty();
        if (sortBy.equals("participantCount")) {
            query.leftJoin(userGathering).on(userGathering.gathering.id.eq(gathering.id))
                .groupBy(
                    gathering.id,
                    user.name
                )
                .orderBy(userGathering.count().desc(), review.createdDate.desc());
        } else if (sortBy.equals("createdDate")) {
            query.orderBy(review.createdDate.desc());
        } else if (sortBy.equals("score")) {
            query.orderBy(review.score.desc(), review.createdDate.desc());
        }

        List<ReviewDetailsResponse> results = query.offset(pageable.getOffset())
            .limit(pageSize + 1L)
            .fetch();

        boolean hasNext = false;
        if (results.size() > pageSize) {
            results.remove(pageSize);
            hasNext = true;
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }

    @Override
    public ReviewScoreCountResponse findReviewScoreCounts(String type, String typeDetail) {
        List<Review> reviews = queryFactory.selectFrom(review)
            .join(review.gathering).fetchJoin()
            .where(gatheringTypeEq(type, typeDetail))
            .fetch();
        return ReviewScoreCountResponse.builder()
            .fivePoints(getScoreCount(reviews, 5))
            .fourPoints(getScoreCount(reviews, 4))
            .threePoints(getScoreCount(reviews, 3))
            .twoPoints(getScoreCount(reviews, 2))
            .onePoints(getScoreCount(reviews, 1))
            .build();
    }

    private JPAQuery<ReviewDetailsResponse> createBaseQuery() {
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
            .join(review.user, user)
            .join(review.gathering, gathering);
    }

    private BooleanExpression gatheringTypeEq(String typeCategory, String typeDetail) {
        Type officeStretching = Type.fromText("오피스 스트레칭");
        Type mindfulness = Type.fromText("마인드풀니스");
        Type worcation = Type.fromText("워케이션");

        if (typeCategory == null) {
            return null;
        }
        if (typeCategory.equals("달램핏")) {
            if (typeDetail == null) {
                return gathering.type.eq(officeStretching).or(gathering.type.eq(mindfulness));
            }
            if (typeDetail.equals("오피스 스트레칭")) {
                return gathering.type.eq(officeStretching);
            }
            if (typeDetail.equals("마인드풀니스")) {
                return gathering.type.eq(mindfulness);
            }
        }
        return gathering.type.eq(worcation);
    }

    private BooleanExpression locationEq(String location) {
        return location != null ? gathering.location.eq(Location.fromText(location)) : null;
    }

    private BooleanExpression dateEq(ZonedDateTime date) {
        if (date == null) {
            return null;
        }
        ZonedDateTime startDateTime = date.toLocalDate().atStartOfDay(date.getZone());
        ZonedDateTime endDateTime = startDateTime.plusDays(1);
        return review.createdDate.goe(startDateTime).and(review.createdDate.lt(endDateTime));
    }

    private int getScoreCount(List<Review> reviews, int score) {
        return (int) reviews.stream()
            .filter(r -> r.getScore() == score)
            .count();
    }
}
