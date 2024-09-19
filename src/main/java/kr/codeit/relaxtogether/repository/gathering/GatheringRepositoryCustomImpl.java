package kr.codeit.relaxtogether.repository.gathering;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import kr.codeit.relaxtogether.dto.gathering.request.GatheringSearchCondition;
import kr.codeit.relaxtogether.dto.gathering.response.QSearchGatheringResponse;
import kr.codeit.relaxtogether.dto.gathering.response.SearchGatheringResponse;
import kr.codeit.relaxtogether.entity.gathering.Location;
import kr.codeit.relaxtogether.entity.gathering.QGathering;
import kr.codeit.relaxtogether.entity.gathering.QUserGathering;
import kr.codeit.relaxtogether.entity.gathering.Type;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

public class GatheringRepositoryCustomImpl implements GatheringRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public GatheringRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Slice<SearchGatheringResponse> searchGatherings(GatheringSearchCondition condition, Pageable pageable) {
        QGathering gathering = QGathering.gathering;
        QUserGathering userGathering = QUserGathering.userGathering;

        List<SearchGatheringResponse> results = queryFactory
            .select(new QSearchGatheringResponse(
                gathering.id,
                gathering.type,
                gathering.name,
                gathering.dateTime,
                gathering.registrationEnd,
                gathering.location,
                userGathering.id.count(),
                gathering.capacity,
                gathering.imageUrl,
                gathering.createdBy.id
            ))
            .from(gathering)
            .leftJoin(userGathering).on(userGathering.gathering.id.eq(gathering.id))
            .where(
                isNotDeleted(),
                categoryEq(condition.getType()),
                locationEq(condition.getLocation()),
                dateBetween(condition.getDate()),
                createdByEq(condition.getCreatedBy())
            )
            .groupBy(
                gathering.id, gathering.type, gathering.name, gathering.dateTime,
                gathering.registrationEnd, gathering.location, gathering.capacity,
                gathering.imageUrl, gathering.createdBy.id
            )
            .orderBy(applySorting(pageable.getSort(), gathering, userGathering))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new SliceImpl<>(results, pageable, results.size() == pageable.getPageSize());
    }

    private OrderSpecifier<?> applySorting(Sort sort, QGathering gathering, QUserGathering userGathering) {
        for (Sort.Order order : sort) {
            if (order.getProperty().equals("registrationEnd")) {
                return new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, gathering.registrationEnd);
            } else if (order.getProperty().equals("participantCount")) {
                return new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, userGathering.id.count());
            }
        }
        return new OrderSpecifier<>(Order.ASC, gathering.registrationEnd);
    }

    private BooleanExpression isNotDeleted() {
        return QGathering.gathering.isDeleted.isFalse();
    }

    private BooleanExpression categoryEq(String category) {
        if (Type.MINDFULNESS.getParentCategory().equalsIgnoreCase(category)) {
            return QGathering.gathering.type.in(Type.OFFICE_STRETCHING, Type.MINDFULNESS);
        }
        return category == null ? null : QGathering.gathering.type.eq(Type.fromText(category));
    }

    private BooleanExpression locationEq(String locationText) {
        if (locationText == null || locationText.isEmpty()) {
            return null;
        }
        try {
            Location location = Location.fromText(locationText);
            return QGathering.gathering.location.eq(location);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private BooleanExpression dateBetween(LocalDate date) {
        if (date == null) {
            return null;
        }
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return QGathering.gathering.dateTime.between(startOfDay, endOfDay);
    }

    private BooleanExpression createdByEq(Long createdBy) {
        return createdBy == null ? null : QGathering.gathering.createdBy.id.eq(createdBy);
    }
}