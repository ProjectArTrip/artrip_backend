package org.atdev.artrip.global.apipayload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.atdev.artrip.global.apipayload.code.BaseCode;
import org.atdev.artrip.global.apipayload.code.status.SuccessStatusCode;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"code", "message"})
public class CommonResponse<T> {

    @JsonProperty("code")
    private final String code;
    private final String message;

    public static <T> CommonResponse<T> onSuccess(T result) {
        return new CommonResponse<>(SuccessStatusCode._OK.getCode(), SuccessStatusCode._OK.getMessage());
    }

    public static <T> CommonResponse<T> of(BaseCode code, T result) {
        return new CommonResponse<>(code.getReasonHttpStatus().getCode(), code.getReasonHttpStatus().getMessage());
    }


    public static <T> CommonResponse<T> onFailure(String code, String message, T data) {
        return new CommonResponse<>(code, message);
    }
}