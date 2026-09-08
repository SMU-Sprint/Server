package smu.sprint.global.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class CustomResponse<T> {

    private static final String SUCCESS_MESSAGE = "요청이 성공적으로 처리되었습니다.";

    @JsonProperty("isSuccess")
    private boolean isSuccess;

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("result")
    private final T result;

    // 성공 응답
    // 기본 세팅값
    public static <T>CustomResponse<T> onSuccess(T result) {
        return new CustomResponse<>(true, String.valueOf(HttpStatus.OK.value()), SUCCESS_MESSAGE, result);
    }

    // 성공 응답
    // HttpStatus를 받아 Success 응답을 반환
    public static <T>CustomResponse<T> onSuccess(HttpStatus status, T result) {
        return new CustomResponse<>(true, String.valueOf(status.value()), SUCCESS_MESSAGE, result);
    }

    // 실패 응답 (데이터 포함)
    // 직접 사용되기 보다는 Exception Handler에서 사용
    public static <T>CustomResponse<T> onFailure(String code, String message, T result) {
        return new CustomResponse<>(false, code, message, result);
    }

    // 실패 응답 (데이터 미포함)
    // 직접 사용되기 보다는 Exception Handler에서 사용
    public static <T>CustomResponse<T> onFailure(String code, String message) {
        return new CustomResponse<>(false, code, message, null);
    }

}
