package smu.sprint.global.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import smu.sprint.global.code.BaseErrorCode;
import smu.sprint.global.code.GeneralErrorCode;
import smu.sprint.global.response.CustomResponse;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // DTO 유효성 검사 실패시 해당 예외를 관련 정보를 함께 CustomResponse로 반환
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<@NonNull CustomResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>(); // 에러 정보는 Map으로 저장
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage())
        );  // 예외에서 정보를 꺼내 각 필드 단위로 Map에 저장 (필드명, 메시지)

        return buildErrorResponse(GeneralErrorCode.VALIDATION_FAILED, errors);
    }

    // Controller 메서드의 파라미터 검증 살패를 처리
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<@NonNull CustomResponse<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        // 검증 실패 목록을 하나씩 꺼내서 처리
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.contains(".") ?
                    propertyPath.substring(propertyPath.lastIndexOf(".") + 1) : propertyPath;
            errors.put(fieldName, violation.getMessage());
        });

        return buildErrorResponse(GeneralErrorCode.VALIDATION_FAILED, errors);
    }

    // 요청 파라미터(@RequestParam 등)가 누락되었을 때 발생하는 예외 처리
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<@NonNull CustomResponse<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex) {
        log.warn("[ MissingServletRequestParameterException ]: '{}' 파라미터가 누락되었습니다.", ex.getParameterName());
        return buildErrorResponse(GeneralErrorCode.BAD_REQUEST_400);
    }

    // 요청 본문(JSON 등)이 비어있거나 형식이 올바르지 않을 때 발생하는 예외 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<@NonNull CustomResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        log.warn("[ HttpMessageNotReadableException ]: {}", ex.getMessage());
        return buildErrorResponse(GeneralErrorCode.BAD_REQUEST_400);
    }

    // 해당 엔드포인트가 지원하지 않는 HTTP 메서드로 요청했을 때 발생하는 예외 처리
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<@NonNull CustomResponse<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {
        log.warn("[ HttpRequestMethodNotSupportedException ]: '{}' 메서드는 지원하지 않습니다.", ex.getMethod());
        return buildErrorResponse(GeneralErrorCode.METHOD_NOT_ALLOWED_405);
    }

    // 애플리케이션에서 발생하는 커스텀 예외를 처리
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<@NonNull CustomResponse<Void>> handleCustomException(CustomException ex) {
        log.warn("[ CustomException ]: {}", ex.getBaseErrorCode().getMessage());
        return buildErrorResponse(ex.getBaseErrorCode());
    }

    // 그 외 발생하는 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NonNull CustomResponse<Void>> handleAllException(Exception ex) {
        log.error("[ Exception ]: Internal Server Error", ex);
        return buildErrorResponse(GeneralErrorCode.INTERNAL_SERVER_ERROR);
    }

    // BaseErrorCode + result(응답에 포함할 데이터)로 ResponseEntity를 생성 (검증 실패 등 부가 데이터가 필요한 경우)
    private <T> ResponseEntity<CustomResponse<T>> buildErrorResponse(BaseErrorCode errorCode, T result) {
        CustomResponse<T> errorResponse = CustomResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), result);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    // BaseErrorCode만으로 처리 가능한 예외들의 ResponseEntity 생성 로직을 공통화 (부가 데이터가 없는 경우)
    private ResponseEntity<CustomResponse<Void>> buildErrorResponse(BaseErrorCode errorCode) {
        return buildErrorResponse(errorCode, null);
    }

}
