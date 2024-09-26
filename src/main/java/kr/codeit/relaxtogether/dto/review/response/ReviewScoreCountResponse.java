package kr.codeit.relaxtogether.dto.review.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewScoreCountResponse {

    private int fivePoints;
    private int fourPoints;
    private int threePoints;
    private int twoPoints;
    private int onePoints;

    @Builder
    public ReviewScoreCountResponse(int fivePoints, int fourPoints, int threePoints, int twoPoints, int onePoints) {
        this.fivePoints = fivePoints;
        this.fourPoints = fourPoints;
        this.threePoints = threePoints;
        this.twoPoints = twoPoints;
        this.onePoints = onePoints;
    }
}
