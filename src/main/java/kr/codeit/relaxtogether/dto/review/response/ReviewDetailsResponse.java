package kr.codeit.relaxtogether.dto.review.response;

import java.time.LocalDateTime;
import kr.codeit.relaxtogether.entity.gathering.Location;
import kr.codeit.relaxtogether.entity.gathering.Type;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewDetailsResponse {

    private String gatheringType;
    private String gatheringLocation;
    private String userProfileImage;
    private String userName;
    private int score;
    private String comment;
    private LocalDateTime createdDate;

    @Builder
    public ReviewDetailsResponse(Type gatheringType, Location gatheringLocation, String userProfileImage,
        String userName, int score, String comment, LocalDateTime createdDate) {
        this.gatheringType = gatheringType.getParentCategory() + " " + gatheringType.getText();
        this.gatheringLocation = gatheringLocation.getText();
        this.userProfileImage = userProfileImage;
        this.userName = userName;
        this.score = score;
        this.comment = comment;
        this.createdDate = createdDate;
    }
}
