package kr.codeit.relaxtogether.entity.gathering;

import static kr.codeit.relaxtogether.exception.ErrorCode.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import kr.codeit.relaxtogether.exception.ApiException;
import kr.codeit.relaxtogether.exception.ErrorCode;

public enum Type {
    OFFICE_STRETCHING("오피스 스트레칭", "달램핏"),
    MINDFULNESS("마인드풀니스", "달램핏"),
    WORKATION("워케이션", "워케이션");

    private final String text;
    private final String parentCategory;

    Type(String text, String parentCategory) {
        this.text = text;
        this.parentCategory = parentCategory;
    }

    public String getText() {
        return text;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    @JsonCreator
    public static Type fromText(String text) {
        return Arrays.stream(Type.values())
            .filter(type -> type.getText().equalsIgnoreCase(text))
            .findFirst()
            .orElseThrow(() -> new ApiException(TYPE_NOT_FOUND));
    }
}
