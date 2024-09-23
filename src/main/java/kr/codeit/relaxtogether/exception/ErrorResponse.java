package kr.codeit.relaxtogether.exception;

import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ErrorResponse {

    private HttpStatus status;
    private String code;
    private String message;
    private Map<String, String> validationErrors;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getHttpStatus();
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(ErrorCode errorCode, Map<String, String> validationErrors) {
        this.status = errorCode.getHttpStatus();
        this.code = errorCode.name();
        this.message = errorCode.getMessage();
        this.validationErrors = validationErrors;
    }
}
