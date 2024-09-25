package kr.codeit.relaxtogether.dto.review.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class GatheringReviewsResponse {

    private List<ReviewDetailsResponse> reviews;
    private int currentPage;
    private int totalPages;
    private long totalElements;

    @Builder
    private GatheringReviewsResponse(List<ReviewDetailsResponse> reviews, int currentPage, int totalPages,
        long totalElements) {
        this.reviews = reviews;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public static GatheringReviewsResponse of(List<ReviewDetailsResponse> reviews, Page<ReviewDetailsResponse> page) {
        return GatheringReviewsResponse.builder()
            .reviews(reviews)
            .currentPage(page.getNumber())
            .totalPages(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .build();
    }
}
