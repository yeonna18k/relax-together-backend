package kr.codeit.relaxtogether.dto.review.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewSearchCondition {

    @Schema(example = "달램핏")
    private String type;

    @Schema(example = "오피스 스트레칭")
    private String typeDetail;

    @Schema(example = "홍대입구")
    private String location;

    private LocalDate date;

    @Builder
    public ReviewSearchCondition(String type, String typeDetail, String location, LocalDate date) {
        this.type = type;
        this.typeDetail = typeDetail;
        this.location = location;
        this.date = date;
    }
}
