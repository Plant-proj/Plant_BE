package dev.plant.backend.global;

import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("정의되지 않은 에러가 발생했습니다.", 500),
    BAD_REQUEST("잘못된 요청입니다.", 400),
    INVALID_INPUT("올바른 입력 형식이 아닙니다",400),
    NOT_FOUND_RESOURCE("존재하지 않는 리소스입니다.", 404),
    CONFLICT_ERROR("중복된 값입니다.", 409),
    //User
    NOT_FOUND_USER("사용자를 찾을 수 없습니다", 404),
    //Transaction
    NOT_FOUND_TRANSACTION("거래 내역을 찾을수 없습니다",404),
    UNAUTHORIZED_TRANSACTION_EDIT("해당 거래내역에 대한 수정 권한이 없습니다",401);

    private final String message;
    private final int status;
}
