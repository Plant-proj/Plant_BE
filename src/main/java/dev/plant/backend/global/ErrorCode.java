package dev.plant.backend.global;

import lombok.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("정의되지 않은 에러가 발생했습니다.", 500),
    BAD_REQUEST("잘못된 요청입니다.", 400),
    NOT_FOUND_RESOURCE("존재하지 않는 리소스입니다.", 404),
    CONFLICT_ERROR("중복된 값입니다.", 409),
    //User
    NOT_FOUND_USER("사용자를 찾을 수 없습니다", 404);

    private final String message;
    private final int status;
}
