package kr.codeit.relaxtogether.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import kr.codeit.relaxtogether.dto.review.request.WriteReviewRequest;
import kr.codeit.relaxtogether.dto.review.response.ReviewDetailsResponse;
import kr.codeit.relaxtogether.entity.Review;
import kr.codeit.relaxtogether.entity.User;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
import kr.codeit.relaxtogether.entity.gathering.Location;
import kr.codeit.relaxtogether.entity.gathering.Type;
import kr.codeit.relaxtogether.repository.UserRepository;
import kr.codeit.relaxtogether.repository.gathering.GatheringRepository;
import kr.codeit.relaxtogether.repository.review.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GatheringRepository gatheringRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @DisplayName("리뷰 작성 시 리뷰 정보가 DB에 저장됩니다.")
    @Test
    void writeReview() {
        // given
        User user = createUser("test@test.com", null);
        Gathering gathering = createGathering("gathering", null, null);

        userRepository.save(user);
        Long gatheringId = gatheringRepository.save(gathering).getId();

        WriteReviewRequest request = WriteReviewRequest.builder()
            .gatheringId(gatheringId)
            .score(5)
            .comment("comment")
            .build();

        // when
        reviewService.writeReview(request, "test@test.com");

        // then
        assertThat(reviewRepository.findAll()).hasSize(1)
            .extracting("user", "gathering", "score", "comment")
            .containsExactlyInAnyOrder(
                tuple(user, gathering, 5, "comment")
            );
    }

    @DisplayName("유저 email을 이용해서 해당 유저가 작성한 리뷰들을 조회합니다.")
    @Test
    void getLoginUserReviews() {
        // given
        User userA = createUser("test@test.com", "userA");
        User userB = createUser("test1@test.com", "userB");
        User userC = createUser("test2@test.com", "userC");

        Gathering gatheringA = createGathering("A", Type.MINDFULNESS, Location.HONGDAE);
        Gathering gatheringB = createGathering("B", Type.OFFICE_STRETCHING, Location.KONDAE);

        userRepository.save(userA).getId();
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
        List<ReviewDetailsResponse> reviews = reviewService.getLoginUserReviews("test@test.com");

        // then
        assertThat(reviews).hasSize(2)
            .extracting(
                "gatheringType", "gatheringLocation", "userProfileImage",
                "userName", "score", "comment"
            )
            .containsExactlyInAnyOrder(
                tuple("달램핏 마인드풀니스", "홍대입구", null, "userA", 5, "good"),
                tuple("달램핏 오피스 스트레칭", "건대입구", null, "userA", 4, "so-so")
            );
    }

    private User createUser(String email, String name) {
        return User.builder()
            .email(email)
            .name(name)
            .build();
    }

    private Gathering createGathering(String name, Type type, Location location) {
        return Gathering.builder()
            .name(name)
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
