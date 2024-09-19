package kr.codeit.relaxtogether.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class PagedResponse<T> {

    private List<T> content;
    private boolean hasNext;
    private long totalElements;

    public PagedResponse(List<T> content, boolean hasNext, long totalElements) {
        this.content = content;
        this.hasNext = hasNext;
        this.totalElements = totalElements;
    }
}
