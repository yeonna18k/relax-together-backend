package kr.codeit.relaxtogether.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import kr.codeit.relaxtogether.dto.review.request.WriteReviewRequest;
import kr.codeit.relaxtogether.entity.User;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
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
        User user = User.builder()
            .email("test@test.com")
            .build();
        Gathering gathering = Gathering.builder()
            .name("gathering")
            .build();

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
}
