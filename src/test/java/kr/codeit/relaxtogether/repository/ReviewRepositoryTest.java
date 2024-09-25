package kr.codeit.relaxtogether.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import kr.codeit.relaxtogether.dto.review.response.ReviewDetailsResponse;
import kr.codeit.relaxtogether.entity.Review;
import kr.codeit.relaxtogether.entity.User;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
import kr.codeit.relaxtogether.entity.gathering.Location;
import kr.codeit.relaxtogether.entity.gathering.Type;
import kr.codeit.relaxtogether.repository.gathering.GatheringRepository;
import kr.codeit.relaxtogether.repository.review.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GatheringRepository gatheringRepository;

    @DisplayName("유저 id와 페이징 처리를 이용해서 해당 유저가 작성한 리뷰들을 조회하고, 다음 리뷰가 있는지 확인합니다.")
    @Test
    void findReviewsByUserId() {
        // given
        User userA = createUser("test@test.com", "userA");
        User userB = createUser("test1@test.com", "userB");
        User userC = createUser("test2@test.com", "userC");

        Gathering gatheringA = createGathering(Type.MINDFULNESS, Location.HONGDAE);
        Gathering gatheringB = createGathering(Type.OFFICE_STRETCHING, Location.KONDAE);

        Long testId = userRepository.save(userA).getId();
        userRepository.save(userB);
        userRepository.save(userC);
        gatheringRepository.save(gatheringA);
        gatheringRepository.save(gatheringB);

        Review reviewA1 = createReview(userA, gatheringA, 5, "good");
        Review reviewA2 = createReview(userA, gatheringB, 4, "so-so");
        Review reviewB1 = createReview(userB, gatheringA, 5, "good");
        Review reviewB2 = createReview(userB, gatheringB, 3, "not bad");
        Review reviewC1 = createReview(userC, gatheringA, 1, "bad");

        reviewRepository.save(reviewA1);
        reviewRepository.save(reviewA2);
        reviewRepository.save(reviewB1);
        reviewRepository.save(reviewB2);
        reviewRepository.save(reviewC1);

        // when
        Slice<ReviewDetailsResponse> reviews = reviewRepository.findReviewsByUserId(testId,
            PageRequest.of(0, 2));

        // then
        assertThat(reviews.getContent()).hasSize(2)
            .extracting(
                "gatheringType", "gatheringLocation", "userProfileImage",
                "userName", "score", "comment"
            )
            .containsExactly(
                tuple("달램핏 오피스 스트레칭", "건대입구", null, "userA", 4, "so-so"),
                tuple("달램핏 마인드풀니스", "홍대입구", null, "userA", 5, "good")
            );
        assertThat(reviews.hasNext()).isFalse();
    }

    @DisplayName("모임 id와 페이징 처리를 이용해서 해당 모임에 작성된 리뷰들을 조회합니다.")
    @Test
    void findReviewsByGatheringId() {
        // given
        User userA = createUser("test@test.com", "userA");
        User userB = createUser("test1@test.com", "userB");
        User userC = createUser("test2@test.com", "userC");

        Gathering gatheringA = createGathering(Type.MINDFULNESS, Location.HONGDAE);
        Gathering gatheringB = createGathering(Type.OFFICE_STRETCHING, Location.KONDAE);

        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);
        Long testId = gatheringRepository.save(gatheringA).getId();
        gatheringRepository.save(gatheringB);

        Review reviewA1 = createReview(userA, gatheringA, 5, "good");
        Review reviewA2 = createReview(userA, gatheringB, 4, "so-so");
        Review reviewB1 = createReview(userB, gatheringA, 5, "good");
        Review reviewB2 = createReview(userB, gatheringB, 3, "not bad");
        Review reviewC1 = createReview(userC, gatheringA, 1, "bad");

        reviewRepository.save(reviewA1);
        reviewRepository.save(reviewA2);
        reviewRepository.save(reviewB1);
        reviewRepository.save(reviewB2);
        reviewRepository.save(reviewC1);

        // when
        List<ReviewDetailsResponse> reviews = reviewRepository.findReviewsByGatheringId(testId, PageRequest.of(0, 3))
            .getReviews();

        // then
        assertThat(reviews).hasSize(3)
            .extracting(
                "gatheringType", "gatheringLocation", "userProfileImage",
                "userName", "score", "comment"
            )
            .containsExactly(
                tuple("달램핏 마인드풀니스", "홍대입구", null, "userC", 1, "bad"),
                tuple("달램핏 마인드풀니스", "홍대입구", null, "userB", 5, "good"),
                tuple("달램핏 마인드풀니스", "홍대입구", null, "userA", 5, "good")
            );
    }

    private User createUser(String email, String name) {
        return User.builder()
            .email(email)
            .name(name)
            .build();
    }

    private Gathering createGathering(Type type, Location location) {
        return Gathering.builder()
            .type(type)
            .location(location)
            .build();
    }

    private Review createReview(User user, Gathering gathering, int score, String comment) {
        return Review.builder()
            .user(user)
            .gathering(gathering)
            .score(score)
            .comment(comment)
            .build();
    }
}
