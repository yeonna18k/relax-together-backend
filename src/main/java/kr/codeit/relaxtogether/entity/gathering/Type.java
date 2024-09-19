package kr.codeit.relaxtogether.entity.gathering;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
            .orElseThrow(() -> new IllegalArgumentException("타입 형식이 올바르지 않습니다.: " + text));
    }
}
