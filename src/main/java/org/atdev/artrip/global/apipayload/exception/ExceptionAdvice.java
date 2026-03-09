package org.atdev.artrip.global.apipayload.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.atdev.artrip.global.apipayload.code.ErrorReasonDTO;
import org.atdev.artrip.global.apipayload.code.status.CommonErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException e, WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();
        e.getConstraintViolations().forEach(v -> {
            String path = v.getPropertyPath().toString();
            String name = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
            errors.put(name, v.getMessage());
        });

        return handleExceptionInternalArgs(
                e, HttpHeaders.EMPTY, CommonErrorCode._BAD_REQUEST, request, errors
        );
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        FieldError firstError = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElse(null);

        String errorMessage;

        if (firstError == null) {
            errorMessage = CommonErrorCode._BAD_REQUEST.getMessage();
        } else if (firstError.getCode() != null && firstError.getCode().contains("typeMismatch")) {
            errorMessage = String.format("%s 필드의 형식이 올바르지 않습니다.", firstError.getField());
        } else {
            errorMessage = firstError.getDefaultMessage();
        }

        CommonResponse<Object> body = CommonResponse.onFailure(
                CommonErrorCode._BAD_REQUEST.getCode(),
                errorMessage,
                null
        );

        return super.handleExceptionInternal(e, body, headers, status, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> exception(Exception e, WebRequest request) {
        log.error("Unhandled exception", e);
        // 내부 상세 메시지는 노출하지 않음
        return handleExceptionInternalFalse(e, CommonErrorCode._INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY, CommonErrorCode._INTERNAL_SERVER_ERROR.getHttpStatus(), request, null);
    }

    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity<Object> onThrowException(GeneralException generalException, HttpServletRequest request) {
        ErrorReasonDTO errorReasonHttpStatus = generalException.getErrorReasonHttpStatus();
        return handleExceptionInternal(generalException, errorReasonHttpStatus, null, request);
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e, ErrorReasonDTO reason,
                                                           HttpHeaders headers, HttpServletRequest request) {

        CommonResponse<Object> body = CommonResponse.onFailure(reason.getCode(), reason.getMessage(), null);
//        e.printStackTrace();

        WebRequest webRequest = new ServletWebRequest(request);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                reason.getHttpStatus(),
                webRequest
        );
    }

    private ResponseEntity<Object> handleExceptionInternalFalse(Exception e, CommonErrorCode errorCommonStatus,
                                                                HttpHeaders headers, HttpStatus status, WebRequest request, String errorPoint) {
        CommonResponse<Object> body = CommonResponse.onFailure(errorCommonStatus.getCode(), errorCommonStatus.getMessage(), errorPoint);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                status,
                request
        );
    }

    private ResponseEntity<Object> handleExceptionInternalArgs(Exception e, HttpHeaders headers, CommonErrorCode errorCommonStatus,
                                                               WebRequest request, Map<String, String> errorArgs) {
        CommonResponse<Object> body = CommonResponse.onFailure(errorCommonStatus.getCode(), errorCommonStatus.getMessage(), errorArgs);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                errorCommonStatus.getHttpStatus(),
                request
        );
    }
}
