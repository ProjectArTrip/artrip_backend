package org.atdev.artrip.controller.dto.response;

import org.atdev.artrip.service.dto.result.PushEnabledResult;

public record PushEnabledResponse(
        Boolean enabled
) {

    public static PushEnabledResponse from(PushEnabledResult result) {
        return new PushEnabledResponse(result.enabled());
    }
}
