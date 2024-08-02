package depth.mvp.ns.global.payload;

import lombok.Getter;

@Getter
public enum ErrorCode {

    /* 400 : BAD REQUEST (잘못된 요청) */
    INVALID_PARAMETER(400, null, "잘못된 요청 데이터 입니다."),
    INVALID_REPRESENTATION(400, null, "잘못된 표현 입니다."),
    INVALID_FILE_PATH(400, null, "잘못된 파일 경로 입니다."),
    INVALID_OPTIONAL_ISPRESENT(400, null, "해당 값이 존재하지 않습니다."),
    INVALID_CHECK(400, null, "해당 값이 유효하지 않습니다."),
    INVALID_AUTHENTICATION(400, null, "잘못된 인증입니다."),

    /* 401 : UNAUTHORIZED (지정한 Resource에 대한 액세스 권한이 없음) */
    UNAUTHORIZED(401, null, "권한이 없습니다."),

    /* 404 : NOT FOUND (Resource를 찾을 수 없음) */
    USER_NOT_FOUND(404, null, "사용자를 찾을 수 없습니다."),
    CONTENTS_NOT_FOUND(404, null, "내용을 찾을 수 없습니다.");

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
