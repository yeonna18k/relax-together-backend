package kr.codeit.relaxtogether.exception;

import static org.springframework.http.HttpStatus.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    UNKNOWN_ERROR(INTERNAL_SERVER_ERROR, "COMMON001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(BAD_REQUEST, "INPUT001", "유효하지 않은 입력값입니다."),

    AUTHENTICATION_FAIL(UNAUTHORIZED, "AUTH001", "해당 유저를 찾을 수 없습니다. 로그인 정보를 확인해 주세요."),
    AUTHORIZATION_FAIL(FORBIDDEN, "AUTH002", "해당 요청에 대한 권한이 없습니다."),

    GATHERING_DATE_VALIDATION_ERROR(BAD_REQUEST, "VALID001", "모집 종료일은 모임 시작일 이전이어야 합니다."),
    GATHERING_NOT_FOUND(NOT_FOUND, "GATH001", "해당 모임을 찾을 수 없습니다."),
    GATHERING_CAPACITY_FULL(BAD_REQUEST, "GATH002", "해당 모임은 이미 정원이 찼습니다."),
    GATHERING_ALREADY_JOINED(BAD_REQUEST, "GATH003", "이미 참여한 모임입니다."),
    GATHERING_CANCELLED(FORBIDDEN, "GATH004", "해당 모임은 취소되었습니다."),
    GATHERING_PAST_DATE(FORBIDDEN, "GATH005", "이미 지난 모임은 참여 취소가 불가합니다."),

    LOCATION_NOT_FOUND(BAD_REQUEST, "LOC001", "장소 이름을 확인해 주세요. [건대입구, 홍대입구, 을지로3가, 신림]"),
    TYPE_NOT_FOUND(BAD_REQUEST, "TYPE001", "타입 형식이 올바르지 않습니다. [오피스 스트레칭, 마인드풀니스, 워케이션]"),

    PARTICIPATION_NOT_FOUND(BAD_REQUEST, "PART001", "참여하지 않은 모임입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
