package kr.codeit.relaxtogether.dto.gathering.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;

@Data
public class GatheringSearchCondition {

    @Schema(description = "타입", allowableValues = {"달램핏", "오피스 스트레칭", "마인드풀니스", "워케이션"})
    private String type;

    @Schema(description = "모임 장소", allowableValues = {"건대입구", "홍대입구", "을지로3가", "신림"})
    private String location;

    @Schema(description = "모임 날짜 (YYYY-MM-DD)")
    private LocalDate date;

    @Schema(description = "모임주최자 Id", example = "1")
    private Long hostUser;
}
