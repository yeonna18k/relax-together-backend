package kr.codeit.relaxtogether.repository.review;

import static kr.codeit.relaxtogether.entity.QReview.review;
import static kr.codeit.relaxtogether.entity.QUser.user;
import static kr.codeit.relaxtogether.entity.gathering.QGathering.gathering;
import static kr.codeit.relaxtogether.entity.gathering.QUserGathering.userGathering;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import kr.codeit.relaxtogether.dto.review.request.ReviewSearchCondition;
import kr.codeit.relaxtogether.dto.review.response.GatheringReviewsResponse;
import kr.codeit.relaxtogether.dto.review.response.ReviewDetailsResponse;
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
        JPAQuery<ReviewDetailsResponse> query = createBaseQuery(pageable);
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
                .orderBy(userGathering.count().desc());
        } else if (sortBy.equals("createdDate")) {
            query.orderBy(review.createdDate.desc());
        } else if (sortBy.equals("score")) {
            query.orderBy(review.score.desc());
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

    private JPAQuery<ReviewDetailsResponse> createBaseQuery(Pageable pageable) {
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

    private BooleanExpression dateEq(LocalDate date) {
        if (date == null) {
            return null;
        }
        LocalDateTime startDateTime = date.atStartOfDay(); // 해당 날짜의 시작
        LocalDateTime endDateTime = date.plusDays(1).atStartOfDay(); // 다음날의 시작
        return review.createdDate.goe(startDateTime).and(review.createdDate.lt(endDateTime));
    }
}
