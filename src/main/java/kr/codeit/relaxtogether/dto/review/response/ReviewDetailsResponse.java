package kr.codeit.relaxtogether.dto.review.response;

import java.time.ZonedDateTime;
import kr.codeit.relaxtogether.entity.gathering.Location;
import kr.codeit.relaxtogether.entity.gathering.Type;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewDetailsResponse {

    private String gatheringType;
    private String gatheringLocation;

    @Setter
    private String gatheringImage;

    private String userProfileImage;
    private String userName;
    private int score;
    private String comment;
    private ZonedDateTime createdDate;

    @Builder
    public ReviewDetailsResponse(Type gatheringType, Location gatheringLocation, String gatheringImage,
        String userProfileImage, String userName, int score, String comment, ZonedDateTime createdDate) {
        if (gatheringType.getParentCategory().equals("워케이션")) {
            this.gatheringType = gatheringType.getParentCategory();
        } else {
            this.gatheringType = gatheringType.getParentCategory() + " " + gatheringType.getText();
        }
        this.gatheringLocation = gatheringLocation.getText();
        this.gatheringImage = gatheringImage;
        this.userProfileImage = userProfileImage;
        this.userName = userName;
        this.score = score;
        this.comment = comment;
        this.createdDate = createdDate;
    }
}
