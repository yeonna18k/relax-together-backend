package kr.codeit.relaxtogether.dto.review.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewSearchCondition {

    @Schema(example = "달램핏")
    private String type;

    @Schema(example = "홍대입구")
    private String location;

    private ZonedDateTime date;

    @Builder
    public ReviewSearchCondition(String type, String location, ZonedDateTime date) {
        this.type = type;
        this.location = location;
        this.date = date;
    }
}
