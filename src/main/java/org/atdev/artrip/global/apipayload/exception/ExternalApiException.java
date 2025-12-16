package org.atdev.artrip.global.apipayload.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ExternalApiException extends RuntimeException {

    private final String apiName;
    private final HttpStatusCode statusCode;
    private final String responseBody;

    public ExternalApiException(String apiName, String message) {
        super(message);
        this.apiName = apiName;
        this.statusCode = null;
        this.responseBody = null;
    }

    public ExternalApiException(String apiName, HttpStatusCode statusCode, String responseBody) {
        super(String.format("[%s] API 호출 실패 - Status: %s, Body: %s", apiName, statusCode, responseBody));
        this.apiName = apiName;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public ExternalApiException(String apiName, String message, Throwable cause) {
        super(message, cause);
        this.apiName = apiName;
        this.statusCode = null;
        this.responseBody = null;
    }
}
