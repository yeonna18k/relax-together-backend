package kr.codeit.relaxtogether.entity.gathering;

import static kr.codeit.relaxtogether.exception.ErrorCode.*;

import java.util.Arrays;
import kr.codeit.relaxtogether.exception.ApiException;
import kr.codeit.relaxtogether.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum Location {
    KONDAE("건대입구"),
    HONGDAE("홍대입구"),
    EULJIRO3GA("을지로3가"),
    SINRIM("신림");

    private final String text;

    Location(String text) {
        this.text = text;
    }

    public static Location fromText(String text) {
        return Arrays.stream(Location.values())
            .filter(location -> location.getText().equalsIgnoreCase(text))
            .findFirst()
            .orElseThrow(() -> new ApiException(LOCATION_NOT_FOUND));
    }
}
