package org.atdev.artrip.controller.dto.response;

import lombok.Getter;

@Getter
public class StatusResponse {
    private final String status;
    private final String greeting;

    public StatusResponse(String status, String greeting) {
        this.status = status;
        this.greeting = greeting;
    }
}
