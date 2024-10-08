package kr.codeit.relaxtogether.dto.review.request;

import jakarta.validation.constraints.Size;
import kr.codeit.relaxtogether.entity.Review;
import kr.codeit.relaxtogether.entity.User;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WriteReviewRequest {

    private Long gatheringId;
    private int score;

    @Size(max = 1000, message = "후기는 1000자 이내로 작성해 주세요.")
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
