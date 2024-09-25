package kr.codeit.relaxtogether.dto.review.request;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewSearchCondition {

    private String type;
    private String typeDetail;
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
