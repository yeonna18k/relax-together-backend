package kr.codeit.relaxtogether.dto.review.request;

import kr.codeit.relaxtogether.entity.Review;
import kr.codeit.relaxtogether.entity.User;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WriteReviewRequest {

    private Long gatheringId;
    private int score;
    private String comment;

    @Builder
    public WriteReviewRequest(Long gatheringId, int score, String comment) {
        this.gatheringId = gatheringId;
        this.score = score;
        this.comment = comment;
    }

    public Review toEntity(User user, Gathering gathering) {
        return Review.builder()
            .user(user)
            .gathering(gathering)
            .score(score)
            .comment(comment)
            .build();
    }
}
